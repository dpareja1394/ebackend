# Gestión de Parqueaderos

Sistema de microservicios para administrar parqueaderos, sus espacios y los vehículos que pertenecen
a cada parqueadero. Proyecto del curso de Arquitectura en la Nube (USB Cali).

> [!WARNING]
> **Esta versión no tiene autenticación.** Cualquiera que alcance el gateway puede crear, modificar
> y eliminar datos. Úsela solo en una máquina local o en una red privada; **no la exponga a
> internet**.

## Arquitectura

```text
                ┌──────────────┐
  navegador ──▶ │  frontend    │  Angular 20 servido por Nginx (:4200)
                │  /api/ ──┐   │
                └──────────┼───┘
                           ▼
                ┌──────────────────┐
                │   API Gateway    │  Spring Cloud Gateway (:8080)
                │  /api/v1/**      │  enruta, CORS, X-Correlation-Id, errores 503/504
                └───┬──────────┬───┘
   /api/v1/vehiculos/**        │ /api/v1/parqueaderos/**, /api/v1/espacios/**
                    ▼          ▼
     ┌──────────────────┐   ┌──────────────────────┐
     │ vehiculos-service│──▶│ parqueaderos-service │
     │ Go 1.26 (:8082)  │◀──│ Spring Boot 3.5 (:8081)
     └────────┬─────────┘   └──────────┬───────────┘
              │   /internal/v1/** (solo entre servicios)
              ▼                        ▼
       PostgreSQL 17             PostgreSQL 17
       (vehiculos)               (parqueaderos)
```

- **parqueaderos-service** es dueño de parqueaderos y espacios, y es el único que cambia el estado
  `OCUPADO` de un espacio.
- **vehiculos-service** es dueño de los vehículos y orquesta la asignación de espacios con una saga
  síncrona: ocupa el espacio en parqueaderos-service y, si no puede guardar la asignación, lo libera
  (compensación con reintentos).
- Cada servicio tiene su propia base de datos; nadie lee la base de otro.
- La API interna (`/internal/v1/**`) no pasa por el gateway.

Detalle de diseño: [plan](specs/001-gestion-parqueaderos/plan.md),
[decisiones](specs/001-gestion-parqueaderos/research.md),
[modelo de datos](specs/001-gestion-parqueaderos/data-model.md) y
[contratos OpenAPI](specs/001-gestion-parqueaderos/contracts/).

## Levantar con Docker Compose

```bash
cp .env.example .env      # ajuste las claves de las bases
docker compose up --build -d
docker compose ps         # todos los servicios deben quedar "healthy"
```

| Componente | URL | Publicado en el host |
|------------|-----|----------------------|
| Frontend | http://localhost:4200 | sí |
| API Gateway | http://localhost:8080/api/v1 | sí |
| parqueaderos-service (Swagger en `/swagger-ui.html`) | http://localhost:8081 | solo con `docker-compose.dev.yml` |
| vehiculos-service | http://localhost:8082 | solo con `docker-compose.dev.yml` |
| PostgreSQL parqueaderos / vehículos | localhost:5433 / 5434 | solo con `docker-compose.dev.yml` |

Modo desarrollo (publica servicios y bases para depurar):

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
```

## Variables de entorno

Todas se definen en `.env` (plantilla en [.env.example](.env.example)).

| Variable | Uso |
|----------|-----|
| `PARQUEADEROS_DB_NAME`, `PARQUEADEROS_DB_USER`, `PARQUEADEROS_DB_PASSWORD` | Base de parqueaderos-service |
| `VEHICULOS_DB_NAME`, `VEHICULOS_DB_USER`, `VEHICULOS_DB_PASSWORD` | Base de vehiculos-service |
| `PARQUEADEROS_URL`, `VEHICULOS_URL` | URLs internas entre servicios y desde el gateway |
| `PARQUEADEROS_TIMEOUT` | Timeout de vehiculos-service al llamar a parqueaderos-service (p. ej. `2s`) |
| `FRONTEND_ORIGIN` | Origen permitido por CORS en el gateway |
| `GATEWAY_URL` | A dónde reenvía Nginx las peticiones `/api/` del frontend |

## Desarrollo sin contenedores

Requiere JDK 21, Go 1.26, Node.js 22 y dos PostgreSQL 17 (puertos 5433 y 5434).

```bash
# parqueaderos-service (Flyway crea las tablas)
(cd services/parqueaderos-service && ./mvnw spring-boot:run)

# vehiculos-service (aplica sus migraciones al arrancar)
(cd services/vehiculos-service && \
  DATABASE_URL='postgres://vehiculos:clave@localhost:5434/vehiculos?sslmode=disable' \
  PARQUEADEROS_URL=http://localhost:8081 go run ./cmd/server)

# gateway
(cd gateway && ./mvnw spring-boot:run)

# frontend (ng serve reenvía /api al gateway con proxy.conf.json)
(cd frontend && npm install && npx ng serve)
```

## Pruebas

```bash
(cd services/parqueaderos-service && ./mvnw verify)   # unitarias + integración (Testcontainers; se omiten sin Docker)
(cd services/vehiculos-service && go test ./...)
(cd frontend && npx ng test --watch=false)
```

La validación manual de punta a punta está en
[quickstart.md](specs/001-gestion-parqueaderos/quickstart.md).

## Observabilidad

- Todos los servicios escriben logs en JSON.
- El gateway asigna un `X-Correlation-Id` (o respeta el que llega), lo propaga a los servicios y lo
  devuelve en la respuesta; el mismo id aparece en los logs de cada servicio que atendió la petición.
- Salud: `/actuator/health` en los servicios Spring y `/health` en vehiculos-service.
- Si un espacio queda ocupado por un fallo parcial entre servicios, se registra `ESPACIO_HUERFANO`
  en los logs; se recupera con `DELETE /api/v1/espacios/{id}/ocupacion` (o el botón "Forzar
  liberación" del frontend), que antes verifica que ningún vehículo lo tenga asignado.
