# Research: Gestión de Parqueaderos, Espacios y Vehículos

**Feature**: `001-gestion-parqueaderos` | **Date**: 2026-10-26

Cada decisión resuelve una incógnita del Technical Context de [plan.md](./plan.md).

## R1. Descomposición en microservicios

- **Decision**: dos microservicios de negocio.
  - `parqueaderos-service` (Spring Boot): parqueaderos **y** espacios.
  - `vehiculos-service` (Go): vehículos y su asignación a espacios.
- **Rationale**: el espacio es parte del agregado parqueadero (código único por parqueadero,
  resumen de disponibilidad, no eliminar parqueadero con espacios); separarlos obligaría a
  consistencia distribuida en reglas que hoy son locales. Los vehículos tienen ciclo de vida
  propio (registro, traslado, búsqueda por placa) y son un buen candidato para Go, cumpliendo
  la preferencia del proyecto de tener servicios en ambos lenguajes (Principio I).
- **Alternatives considered**:
  - Tres servicios (parqueaderos, espacios, vehículos): rechazado; espacios sin parqueadero no
    tienen sentido y duplicaría llamadas remotas para cada validación.
  - Un solo servicio: rechazado; no ejercita la arquitectura de microservicios ni Go.

## R2. Consistencia de la asignación vehículo ↔ espacio entre servicios

- **Decision**: `vehiculos-service` orquesta la asignación con llamadas síncronas a una API
  **interna** de `parqueaderos-service`:
  1. `POST /internal/v1/espacios/{id}/ocupacion` con `vehiculoId`, `parqueaderoId` y
     `tipoVehiculo`. El servicio de parqueaderos valida (mismo parqueadero, parqueadero activo,
     tipo compatible, estado disponible) y ocupa con un `UPDATE ... WHERE estado='DISPONIBLE'`
     condicional (atómico).
  2. Si responde OK, `vehiculos-service` guarda `espacioId`/`espacioCodigo` en el vehículo.
  3. Si el paso 2 falla, compensa con `DELETE /internal/v1/espacios/{id}/ocupacion`.
  - Liberar: primero se libera el espacio remoto y luego se limpia el vehículo (liberar es
    idempotente, así que reintentar es seguro).
  - Reasignar: ocupar el nuevo → actualizar vehículo → liberar el anterior.
  - Las liberaciones de compensación se reintentan hasta 3 veces (200 ms). Eliminar un vehículo
    borra la fila en una transacción que solo se confirma si la liberación remota tuvo éxito.
  - **Recuperación de huérfanos**: si aun así un espacio queda `OCUPADO` por un vehículo que ya
    no existe o no lo referencia, se registra `ESPACIO_HUERFANO` en el log y el administrador
    puede usar `DELETE /api/v1/espacios/{id}/ocupacion`. `parqueaderos-service` verifica con
    `GET /internal/v1/vehiculos/{id}` antes de liberar, así que el estado sigue controlado por
    el sistema (FR-007).
- **Rationale**: el estado "ocupado" lo controla solo el sistema (FR-007) y la condición en el
  `UPDATE` resuelve la carrera de dos administradores (edge case) sin locks distribuidos. Es el
  patrón saga más simple posible (Principio V).
- **Alternatives considered**:
  - Mensajería/eventos (RabbitMQ/Kafka): rechazado por complejidad para este alcance; queda
    como evolución futura.
  - Que el frontend llame a ambos servicios: rechazado; rompe la atomicidad y expone la API
    interna.

## R3. Validaciones entre servicios (existencia de parqueadero, eliminación)

- **Decision**:
  - Al registrar/trasladar un vehículo, `vehiculos-service` consulta
    `GET /internal/v1/parqueaderos/{id}` para verificar existencia.
  - Al eliminar un parqueadero, `parqueaderos-service` consulta
    `GET /internal/v1/vehiculos/conteo?parqueaderoId={id}`; si hay vehículos (o el servicio
    no responde) se rechaza la eliminación.
  - Todas las llamadas internas usan timeout de 2 s; ante falla se responde `503` con el formato
    de error estándar (fail-safe: nunca se elimina sin confirmar).
- **Rationale**: cumple FR-003/FR-013 y el Principio IV (timeouts, fallos controlados).
- **Alternatives considered**: réplica local de parqueaderos en `vehiculos-service` vía eventos;
  rechazado por ahora (YAGNI).

## R4. API Gateway

- **Decision**: Spring Cloud Gateway (Spring Boot 3.5.x, reactivo) como punto único de entrada
  en el puerto 8080. Enruta por prefijo y agrega/propaga `X-Correlation-Id`. Las rutas
  `/internal/**` **no** se exponen. CORS para el frontend se configura solo en el gateway.
- **Rationale**: mismo ecosistema que el servicio Java, configuración declarativa de rutas,
  filtros globales para el correlation id; requerido por Principio II.
- **Alternatives considered**: Nginx (sin lógica de filtros cómoda), Kong/Traefik (otro
  componente que aprender y operar), KrakenD.

## R5. Stack de `parqueaderos-service`

- **Decision**: Java 21, Spring Boot 3.5.x, Spring Web, Spring Data JPA, Bean Validation,
  Flyway, Spring Boot Actuator, springdoc-openapi, RestClient con timeouts para llamadas
  internas; Maven.
- **Rationale**: stack fijado por la constitución y conocido por el equipo.
- **Alternatives considered**: WebFlux (innecesario para CRUD), MyBatis.

## R6. Stack de `vehiculos-service`

- **Decision**: Go 1.26, `net/http` estándar (enrutamiento con patrones de Go 1.22+),
  `pgx/v5` (pool) para PostgreSQL, `golang-migrate` para migraciones embebidas, `log/slog`
  con handler JSON, `net/http` client con timeout para llamadas internas.
- **Rationale**: mínima dependencia (Principio V), logs JSON nativos (Principio IV), imagen
  pequeña (binario estático en `distroless`/`alpine`).
- **Alternatives considered**: Gin/Echo (no aportan lo suficiente con el router de 1.22+),
  GORM (ORM innecesario para 1 tabla).

## R7. Persistencia

- **Decision**: PostgreSQL 17, **una instancia/contenedor por servicio**: `db-parqueaderos`
  y `db-vehiculos` (Principio I, datos propios). Unicidades con índices únicos:
  `(lower(nombre), lower(ciudad))`, `(parqueadero_id, codigo)`, `placa` normalizada.
- **Rationale**: los índices únicos garantizan las reglas aún con concurrencia.
- **Alternatives considered**: una instancia con dos esquemas (menos aislamiento); MongoDB
  (los datos son relacionales).

## R8. Frontend

- **Decision**: Angular 20 (standalone components, signals, Angular Router, HttpClient,
  Reactive Forms), servido por Nginx en contenedor. Usa la ruta relativa `/api/v1`; Nginx
  la reenvía al gateway (`GATEWAY_URL` por variable de entorno, plantilla `envsubst`) y
  `ng serve` usa `proxy.conf.json`. Así no hay URL de entorno en el código (Principio III). Módulos de vista: parqueaderos, espacios (dentro del detalle del parqueadero),
  vehículos.
- **Rationale**: fijado por constitución; standalone reduce boilerplate.
- **Alternatives considered**: Angular Material es opcional; se usa si acelera formularios y
  tablas.

## R9. Formato de error y códigos HTTP

- **Decision**: cuerpo uniforme en ambos servicios:
  `{ "codigo", "mensaje", "detalles": [{campo, mensaje}], "correlationId", "timestamp" }`.
  - `400 VALIDACION` — datos faltantes o inválidos.
  - `404 NO_ENCONTRADO` — recurso inexistente.
  - `409 CONFLICTO` — duplicados (nombre, código, placa) y estado incompatible (espacio ocupado,
    parqueadero con dependencias, vehículo ya asignado).
  - `422 REGLA_NEGOCIO` — tipo incompatible, parqueadero inactivo, espacio de otro parqueadero.
  - `503 SERVICIO_NO_DISPONIBLE` — dependencia interna caída o con timeout.
- **Rationale**: FR-019 y Principio II.

## R10. Paginación y normalización

- **Decision**: listados con `?pagina=0&tamano=20` (máx. 100) y respuesta
  `{ contenido, pagina, tamano, totalElementos, totalPaginas }` igual en ambos servicios.
  La placa se normaliza con `upper(remove [\s-])` en `vehiculos-service` antes de validar y
  buscar.
- **Rationale**: contrato homogéneo para el frontend; soporta SC-003 (10.000 vehículos).

## R11. Pruebas (recomendadas, Principio V)

- **Decision**: JUnit 5 + Spring Boot Test + Testcontainers (PostgreSQL) para
  `parqueaderos-service`; `testing` + `httptest` para `vehiculos-service` (cliente de
  parqueaderos simulado); pruebas de componentes Angular con el runner por defecto del CLI.
  Prioridad: asignación concurrente, reglas de eliminación, normalización de placa.
- **Rationale**: cubre los puntos de mayor riesgo sin bloquear entregas.

## R12. Observabilidad

- **Decision**: health en `/actuator/health` (Java) y `/health` (Go) usados por
  `healthcheck` de Docker Compose; logs JSON (Logback JSON encoder en Java, gateway incluido;
  `slog` en Go) incluyendo `correlationId`. El gateway genera el id si no viene y, como es
  reactivo y no propaga el MDC, escribe su log de acceso con `correlationId` como campo
  explícito.
- **Rationale**: Principio IV.
