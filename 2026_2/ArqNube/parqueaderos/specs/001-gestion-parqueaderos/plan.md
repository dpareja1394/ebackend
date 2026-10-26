# Implementation Plan: Gestión de Parqueaderos, Espacios y Vehículos

**Branch**: `001-gestion-parqueaderos` | **Date**: 2026-10-26 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-gestion-parqueaderos/spec.md`

## Summary

Administración de parqueaderos, sus espacios y los vehículos registrados en ellos, con
asignación de un vehículo a un espacio compatible. Se implementa como:

- `parqueaderos-service` (Spring Boot): parqueaderos, espacios, resumen de disponibilidad y
  ocupación atómica de espacios.
- `vehiculos-service` (Go): vehículos, búsqueda por placa, traslado y orquestación de la
  asignación (saga síncrona con compensación).
- `gateway` (Spring Cloud Gateway): único punto de entrada, correlation id y CORS.
- `frontend` (Angular): pantallas de administración.

Todo se ejecuta en contenedores con `docker compose`. Ver decisiones en [research.md](./research.md).

## Technical Context

**Language/Version**: Java 21 (parqueaderos-service, gateway); Go 1.26 (vehiculos-service);
TypeScript con Angular 20 (frontend)

**Primary Dependencies**: Spring Boot 3.5.x (Web, Data JPA, Validation, Actuator), Flyway,
springdoc-openapi, Spring Cloud Gateway; Go `net/http`, `pgx/v5`, `golang-migrate`,
`log/slog`; Angular Router, HttpClient, Reactive Forms

**Storage**: PostgreSQL 17, una instancia por servicio (`db-parqueaderos`, `db-vehiculos`)

**Testing**: recomendadas — JUnit 5 + Testcontainers; `go test` + `httptest`; pruebas de
componentes del Angular CLI

**Target Platform**: contenedores Linux (Docker Compose local; desplegable en cualquier nube)

**Project Type**: web application distribuida (microservicios + API Gateway + SPA)

**Performance Goals**: búsqueda por placa < 2 s con 10.000 vehículos (SC-003); operaciones
CRUD percibidas como inmediatas (< 1 s) por el administrador

**Constraints**: timeouts de 2 s en llamadas internas y 5 s en el gateway; ninguna
operación rechazada deja datos parciales (FR-020); asignación concurrente segura

**Scale/Scope**: decenas de parqueaderos, miles de espacios, ~10.000 vehículos; pocos
administradores concurrentes; ~8 pantallas

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio | Verificación | Pre | Post-diseño |
|-----------|--------------|-----|-------------|
| I. Microservicios Autónomos | 2 servicios con responsabilidad de negocio clara; cada uno con su base; comunicación solo por API; elección SB/Go justificada (R1) | ✅ | ✅ |
| II. Contratos primero + Gateway | OpenAPI en `contracts/`; rutas `/api/v1`; frontend solo vía gateway; formato de error uniforme (R9); `/internal` no expuesto; solo gateway y frontend publican puertos en el host | ✅ | ✅ |
| III. Contenedores y config externa | Dockerfile por componente propio; PostgreSQL con imagen oficial `17-alpine` fija (v1.0.1); `docker compose up` levanta todo; config/secretos en `.env`; frontend con ruta relativa `/api/v1` y `GATEWAY_URL` por env; servicios sin estado | ✅ | ✅ |
| IV. Observabilidad y resiliencia | `/actuator/health` y `/health`; logs JSON en los tres backends (gateway incluido); `X-Correlation-Id` propagado; timeouts, `503` controlado y recuperación de espacios huérfanos (R2, R3, R12) | ✅ | ✅ |
| V. Simplicidad y calidad pragmática | Sin mensajería ni caché; saga síncrona mínima; validaciones antes de persistir; pruebas recomendadas en puntos de riesgo | ✅ | ✅ |
| Stack | Spring Boot 3.x/Java 21, Go 1.22+, Angular, PostgreSQL, Docker Compose, directorios separados | ✅ | ✅ |

Sin violaciones; la tabla de Complexity Tracking queda vacía.

## Project Structure

### Documentation (this feature)

```text
specs/001-gestion-parqueaderos/
├── plan.md              # Este archivo
├── research.md          # Fase 0
├── data-model.md        # Fase 1
├── quickstart.md        # Fase 1
├── contracts/
│   ├── parqueaderos-service.openapi.yaml
│   ├── vehiculos-service.openapi.yaml
│   └── gateway-routes.md
└── tasks.md             # Fase 2 (/speckit-tasks)
```

### Source Code (repository root)

```text
docker-compose.yml                         # solo gateway (8080) y frontend (4200) publican puertos
docker-compose.dev.yml                     # desarrollo: publica 8081/8082 (Swagger) y bases
.env.example

gateway/                                   # Spring Cloud Gateway (Java 21, Maven)
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/co/edu/usbcali/gateway/
    │   ├── GatewayApplication.java
    │   ├── filter/CorrelationIdFilter.java
    │   └── error/GatewayErrorHandler.java
    └── resources/
        ├── application.yml                # rutas, CORS, timeouts
        └── logback-spring.xml             # logs JSON

services/parqueaderos-service/             # Spring Boot (Java 21, Maven)
├── Dockerfile
├── pom.xml
└── src/
    ├── main/java/co/edu/usbcali/parqueaderos/
    │   ├── domain/          # Parqueadero, Espacio, enums
    │   ├── repository/
    │   ├── service/         # reglas de negocio, resumen, ocupación
    │   ├── controller/      # /api/v1 y /internal/v1
    │   ├── dto/
    │   ├── client/          # VehiculosClient (RestClient con timeout)
    │   ├── exception/       # manejador global → formato de error estándar
    │   └── config/          # correlation id (MDC), logging JSON
    ├── main/resources/
    │   ├── application.yml
    │   └── db/migration/    # Flyway V1__...
    └── test/java/co/edu/usbcali/parqueaderos/

services/vehiculos-service/                # Go 1.26
├── Dockerfile
├── go.mod
├── cmd/server/main.go
├── internal/
│   ├── vehiculo/            # modelo, normalización de placa, servicio (saga), repositorio pgx
│   ├── httpapi/             # handlers /api/v1 y /internal/v1, errores, middleware correlation id
│   ├── parqueaderos/        # cliente HTTP con timeout hacia parqueaderos-service
│   └── platform/            # config por env, pool de BD, logger slog
├── migrations/              # SQL embebido (golang-migrate)
└── internal/**/*_test.go

frontend/                                  # Angular 20
├── Dockerfile               # build + Nginx
├── nginx/default.conf.template   # SPA + proxy /api/ → ${GATEWAY_URL}
├── proxy.conf.json          # proxy de ng serve hacia el gateway
└── src/app/
    ├── core/                # ApiService base, interceptor de errores, modelos
    ├── features/
    │   ├── parqueaderos/    # listado, formulario, detalle + resumen
    │   ├── espacios/        # gestión dentro del detalle del parqueadero
    │   └── vehiculos/       # listado por parqueadero, búsqueda por placa, formulario, asignación
    └── shared/
```

**Structure Decision**: estructura multi-componente con un directorio por despliegue
(`gateway/`, `services/<nombre>/`, `frontend/`), según la sección Stack de la constitución.
Los componentes solo comparten los contratos de `contracts/`; no hay código compartido entre
servicios.

## Complexity Tracking

No aplica: el Constitution Check no tiene violaciones.
