---

description: "Task list for 001-gestion-parqueaderos"
---

# Tasks: Gestión de Parqueaderos, Espacios y Vehículos

**Input**: Design documents from `/specs/001-gestion-parqueaderos/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: la spec no pide TDD y la constitución (Principio V) hace las pruebas
**recomendadas, no bloqueantes**. Por eso no hay tareas de prueba dentro de las historias;
se agrupan como tareas recomendadas en la fase final (T075–T077).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Paquete Java servicio: `services/parqueaderos-service/src/main/java/co/edu/usbcali/parqueaderos/` → abreviado **`PS/`**
- Recursos servicio: `services/parqueaderos-service/src/main/resources/` → **`PS-RES/`**
- Paquete Java gateway: `gateway/src/main/java/co/edu/usbcali/gateway/` → **`GW/`**
- Servicio Go: `services/vehiculos-service/` → **`VS/`**
- Angular: `frontend/src/app/` → **`FE/`**
- Contratos de referencia: `specs/001-gestion-parqueaderos/contracts/`; formato de error, paginación y
  códigos HTTP según `research.md` R9/R10.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Crear la estructura de directorios `gateway/`, `services/parqueaderos-service/`, `services/vehiculos-service/`, `frontend/` y un `.gitignore` raíz (target/, node_modules/, dist/, .angular/, .env, *.iml, .idea/, .vscode/) en `.gitignore`
- [X] T002 [P] Inicializar el proyecto Maven de parqueaderos-service en `services/parqueaderos-service/pom.xml` (parent Spring Boot 3.5.x, Java 21; dependencias: spring-boot-starter-web, -data-jpa, -validation, -actuator, flyway-core, flyway-database-postgresql, postgresql, springdoc-openapi-starter-webmvc-ui, net.logstash.logback:logstash-logback-encoder; test: spring-boot-starter-test, spring-boot-testcontainers, testcontainers postgresql), Maven Wrapper (`mvnw`) y clase principal `PS/ParqueaderosServiceApplication.java`
- [X] T003 [P] Inicializar el proyecto Maven del gateway en `gateway/pom.xml` (parent Spring Boot 3.5.x, Java 21, BOM Spring Cloud 2025.0.x; dependencias: spring-cloud-starter-gateway (reactivo), spring-boot-starter-actuator, logstash-logback-encoder), Maven Wrapper y clase `GW/GatewayApplication.java`
- [X] T004 [P] Inicializar el módulo Go en `services/vehiculos-service/go.mod` (módulo `parqueaderos/vehiculos-service`, Go 1.26; dependencias `github.com/jackc/pgx/v5`, `github.com/golang-migrate/migrate/v4` con drivers `pgx/v5` y fuente `iofs`, `github.com/google/uuid`) y un `VS/cmd/server/main.go` mínimo que compile
- [X] T005 [P] Generar el proyecto Angular 20 en `frontend/` (`ng new frontend --standalone --routing --style=scss --ssr=false --skip-git`) y definir la ruta **relativa** `apiBaseUrl: '/api/v1'` en `frontend/src/environments/environment.ts` (sin host ni puerto: ningún valor de entorno en el código, Principio III); para `ng serve` crear `frontend/proxy.conf.json` que reenvía `/api` a `http://localhost:8080` y referenciarlo en `angular.json` (`serve.options.proxyConfig`)
- [X] T006 [P] Crear `services/parqueaderos-service/Dockerfile` multi-stage (build `maven:3.9-eclipse-temurin-21` con `mvn -DskipTests package`; runtime `eclipse-temurin:21-jre-alpine`, usuario no root, `EXPOSE 8081`)
- [X] T007 [P] Crear `gateway/Dockerfile` multi-stage (mismo patrón que T006, `EXPOSE 8080`)
- [X] T008 [P] Crear `services/vehiculos-service/Dockerfile` multi-stage (build `golang:1.26-alpine` con `CGO_ENABLED=0 go build -o /app ./cmd/server`; runtime `alpine:3.22` con `wget` para healthcheck, usuario no root, `EXPOSE 8082`)
- [X] T009 [P] Crear `frontend/Dockerfile` (build `node:22-alpine` con `npm ci && npx ng build`; runtime `nginx:1.27-alpine`) y la plantilla `frontend/nginx/default.conf.template` (copiada a `/etc/nginx/templates/`, que la imagen oficial procesa con `envsubst` al arrancar) con fallback SPA `try_files $uri $uri/ /index.html` y `location /api/ { proxy_pass ${GATEWAY_URL}; }` reenviando `X-Correlation-Id`; `GATEWAY_URL` llega por variable de entorno
- [X] T010 Crear `docker-compose.yml` con: `db-parqueaderos` y `db-vehiculos` (imagen oficial con versión fija `postgres:17-alpine`, sin Dockerfile propio según Principio III v1.0.1; volumen nombrado propio, healthcheck `pg_isready`), `parqueaderos-service` (`expose: 8081`, `depends_on` db healthy, env `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`, `VEHICULOS_URL=http://vehiculos-service:8082`), `vehiculos-service` (`expose: 8082`, `DATABASE_URL`, `PARQUEADEROS_URL=http://parqueaderos-service:8081`), `gateway` (`ports: 8080:8080`, `PARQUEADEROS_URL`, `VEHICULOS_URL`, `FRONTEND_ORIGIN`), `frontend` (`ports: 4200:80`, `GATEWAY_URL=http://gateway:8080`, `depends_on` gateway); **solo el gateway y el frontend publican puertos en el host** — servicios y bases usan `expose` para que `/internal/**` y las APIs no sean alcanzables sin pasar por el gateway (Principio II); healthcheck por servicio (`/actuator/health` en Java, `/health` en Go); todas las variables leídas de `.env`
- [X] T011 [P] Crear `.env.example` con todas las variables usadas en `docker-compose.yml` (usuarios/contraseñas/nombres de BD de ejemplo, URLs internas, `FRONTEND_ORIGIN`, `GATEWAY_URL`) sin secretos reales
- [X] T012 [P] Crear `docker-compose.dev.yml` (solo desarrollo, se usa con `docker compose -f docker-compose.yml -f docker-compose.dev.yml up`) que publica `8081:8081` (Swagger de parqueaderos-service), `8082:8082`, `5433:5432` (db-parqueaderos) y `5434:5432` (db-vehiculos); no usar el nombre `docker-compose.override.yml`, que Compose carga automáticamente

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura común de los tres componentes backend y del frontend

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### parqueaderos-service

- [X] T013 Configurar `PS-RES/application.yml`: `server.port: 8081`; datasource desde env; `spring.jpa.hibernate.ddl-auto: validate`; `spring.jpa.open-in-view: false`; Flyway habilitado; actuator exponiendo `health`; springdoc en `/swagger-ui.html`; propiedades `vehiculos.url: ${VEHICULOS_URL:http://localhost:8082}` y `vehiculos.timeout: 2s`
- [X] T014 [P] Crear `PS-RES/logback-spring.xml` con `LogstashEncoder` a stdout (JSON) incluyendo el campo MDC `correlationId`
- [X] T015 [P] Implementar `PS/config/CorrelationIdFilter.java` (`OncePerRequestFilter`, orden más alto): lee `X-Correlation-Id` o genera UUID, lo guarda en MDC `correlationId`, lo agrega a la respuesta y limpia el MDC al final
- [X] T016 [P] Crear enums de dominio en `PS/domain/TipoVehiculo.java` (`AUTOMOVIL`, `MOTOCICLETA`, `BICICLETA`), `PS/domain/EstadoParqueadero.java` (`ACTIVO`, `INACTIVO`) y `PS/domain/EstadoEspacio.java` (`DISPONIBLE`, `OCUPADO`, `FUERA_DE_SERVICIO`)
- [X] T017 [P] Crear el manejo de errores en `PS/exception/`: `CodigoError` (enum `VALIDACION`, `NO_ENCONTRADO`, `CONFLICTO`, `REGLA_NEGOCIO`, `SERVICIO_NO_DISPONIBLE`, `ERROR_INTERNO`), record `ErrorResponse(codigo, mensaje, List<DetalleError(campo, mensaje)> detalles, correlationId, timestamp)`, excepciones `RecursoNoEncontradoException` (404), `ConflictoException` (409), `ReglaNegocioException` (422), `ServicioNoDisponibleException` (503), `ValidacionException` (400, con detalles) y `GlobalExceptionHandler` (`@RestControllerAdvice`) que mapea además `MethodArgumentNotValidException`/`HandlerMethodValidationException`/`HttpMessageNotReadableException`/`MethodArgumentTypeMismatchException` → 400 `VALIDACION` con `detalles[].campo`, `DataIntegrityViolationException` → 409 `CONFLICTO`, y cualquier otra → 500 `ERROR_INTERNO`; `correlationId` tomado del MDC
- [X] T018 [P] Crear `PS/dto/PaginaResponse.java` (record genérico `contenido, pagina, tamano, totalElementos, totalPaginas` con `static from(Page<T>)`) y `PS/dto/Paginacion.java` que valida `pagina >= 0` y `1 <= tamano <= 100` (por defecto 0 y 20; fuera de rango → `ValidacionException`) y produce un `PageRequest`
- [X] T019 Implementar `PS/client/VehiculosClient.java` con `RestClient` (base `vehiculos.url`, connect y read timeout `vehiculos.timeout`), reenvía `X-Correlation-Id` del MDC; método `long contarPorParqueadero(UUID parqueaderoId)` → `GET /internal/v1/vehiculos/conteo?parqueaderoId=` leyendo `cantidad`; cualquier error de red, timeout o 5xx lanza `ServicioNoDisponibleException("El servicio de vehículos no está disponible")`. (En US3 se agrega `obtenerVehiculo`, ver T062)

### vehiculos-service

- [X] T020 [P] Implementar `VS/internal/platform/config.go`: struct `Config` desde env (`PORT` def. `8082`, `DATABASE_URL` obligatorio, `PARQUEADEROS_URL` def. `http://localhost:8081`, `PARQUEADEROS_TIMEOUT` def. `2s`)
- [X] T021 [P] Implementar `VS/internal/platform/logger.go`: `slog` con `JSONHandler` a stdout; helpers `WithCorrelationID(ctx, id)`, `CorrelationID(ctx)` y `Logger(ctx)` que agrega el atributo `correlationId`
- [X] T022 [P] Crear la migración `VS/migrations/000001_crear_vehiculos.up.sql` (y `.down.sql`): tabla `vehiculos` con `id uuid PK`, `placa varchar(10) NOT NULL` con índice único y además índice `varchar_pattern_ops` para búsqueda por prefijo (`placa LIKE 'X%'`, FR-018), `tipo varchar(20) NOT NULL CHECK (tipo IN ('AUTOMOVIL','MOTOCICLETA','BICICLETA'))`, `marca varchar(50) NULL`, `color varchar(30) NOT NULL`, `propietario_nombre varchar(100) NOT NULL`, `propietario_documento varchar(20) NOT NULL`, `parqueadero_id uuid NOT NULL` (índice), `espacio_id uuid NULL` con índice único parcial `WHERE espacio_id IS NOT NULL`, `espacio_codigo varchar(20) NULL`, `creado_en`/`actualizado_en timestamptz NOT NULL DEFAULT now()`; y `VS/migrations/embed.go` con `//go:embed *.sql`
- [X] T023 Implementar `VS/internal/platform/db.go`: crea `pgxpool.Pool` desde `DATABASE_URL` y ejecuta las migraciones embebidas (golang-migrate `iofs` + driver `pgx/v5`) al arrancar (depende de T022)
- [X] T024 [P] Definir errores de dominio en `VS/internal/vehiculo/errors.go`: tipo `Error{Codigo, Mensaje, Detalles []Detalle{Campo, Mensaje}}` y constructores `ErrValidacion`, `ErrNoEncontrado`, `ErrConflicto`, `ErrReglaNegocio`, `ErrServicioNoDisponible` (códigos iguales a R9)
- [X] T025 Implementar `VS/internal/httpapi/errors.go`: `writeJSON`, `writeError` con el cuerpo `{codigo, mensaje, detalles, correlationId, timestamp}` y mapeo de `vehiculo.Error` → HTTP (400/404/409/422/503; otro error → 500 `ERROR_INTERNO`) (depende de T024)
- [X] T026 [P] Implementar `VS/internal/httpapi/middleware.go`: middleware de correlation id (lee `X-Correlation-Id` o genera UUID, lo pone en el contexto y en la respuesta), log de acceso JSON (método, ruta, status, duración) y recuperación de panics → 500 `ERROR_INTERNO`
- [X] T027 Implementar `VS/internal/vehiculo/repository.go` con struct `PgRepository` (pgxpool) y el método `ContarPorParqueadero(ctx, parqueaderoID uuid.UUID) (int64, error)`
- [X] T028 Implementar `VS/internal/httpapi/internal_handlers.go` con `GET /internal/v1/vehiculos/conteo?parqueaderoId=` (UUID obligatorio y válido → si no, 400) que responde `{parqueaderoId, cantidad}`, y `VS/internal/httpapi/router.go` con `http.ServeMux` (patrones Go 1.22), `GET /health` (hace `Ping` a la BD; 200 `{"status":"UP"}` o 503) y los middlewares de T026
- [X] T029 Completar `VS/cmd/server/main.go`: carga config, logger, pool + migraciones, router; `http.Server` con `ReadHeaderTimeout` y apagado ordenado con `SIGINT/SIGTERM`

### gateway

- [X] T030 Configurar `gateway/src/main/resources/application.yml` según `contracts/gateway-routes.md`: `server.port: 8080`; ruta `vehiculos` (orden 1, `Path=/api/v1/vehiculos,/api/v1/vehiculos/**`, uri `${VEHICULOS_URL}`); ruta `parqueaderos` (orden 2, `Path=/api/v1/parqueaderos,/api/v1/parqueaderos/**,/api/v1/espacios/**`, uri `${PARQUEADEROS_URL}`); sin ruta para `/internal/**`; `globalcors` para `${FRONTEND_ORIGIN:http://localhost:4200}` con métodos `GET, POST, PUT, DELETE, OPTIONS` y exponiendo `X-Correlation-Id` y `Location`; `httpclient.connect-timeout: 2000`, `response-timeout: 5s`; actuator `health`
- [X] T031 [P] Implementar `GW/filter/CorrelationIdFilter.java` (`GlobalFilter` + `Ordered.HIGHEST_PRECEDENCE`): genera UUID si falta `X-Correlation-Id`, lo agrega a la petición reenviada y a la respuesta, y al completar la respuesta registra un log de acceso con `correlationId`, método, ruta, servicio destino, status y duración como campos estructurados (`StructuredArguments.kv`), porque en el gateway reactivo el MDC no se propaga solo
- [X] T032 [P] Implementar `GW/error/GatewayErrorHandler.java` (`ErrorWebExceptionHandler` con orden `-2`): devuelve el formato de error estándar; conexión rechazada/servicio caído → 503 `SERVICIO_NO_DISPONIBLE`, timeout → 504 `SERVICIO_NO_DISPONIBLE`, ruta no encontrada → 404 `NO_ENCONTRADO`
- [X] T033 [P] Crear `gateway/src/main/resources/logback-spring.xml` con `LogstashEncoder` a stdout (logs JSON, Principio IV), igual que T014, para que los logs de T031 y T032 salgan estructurados

### frontend

- [X] T034 [P] Crear `FE/core/models/api.models.ts` con `Pagina<T>`, `ErrorResponse`, `DetalleError`, tipos `TipoVehiculo`, `EstadoParqueadero`, `EstadoEspacio` y sus etiquetas en español para la UI
- [X] T035 [P] Crear `FE/core/services/notificacion.service.ts` (signal con lista de mensajes éxito/error, autocierre 5 s) y el componente `FE/shared/components/notificaciones/notificaciones.component.ts` que los muestra
- [X] T036 Crear `FE/core/interceptors/error.interceptor.ts` (funcional) que convierte `HttpErrorResponse` en `ErrorResponse` (status 0 → "No se pudo conectar con el servidor") y publica `mensaje` en `NotificacionService` (FR-019); registrarlo con `provideHttpClient(withInterceptors([...]))` en `FE/app.config.ts`
- [X] T037 Crear el layout en `FE/app.component.ts`/`.html`/`.scss` (barra de navegación con "Parqueaderos" y "Vehículos", `<router-outlet>`, `<app-notificaciones>`) y `FE/app.routes.ts` con rutas lazy `parqueaderos` y `vehiculos` (redirección por defecto a `parqueaderos`)

**Checkpoint**: `docker compose up --build` levanta todo con healthchecks en verde; el gateway responde 404 a `/internal/**`

---

## Phase 3: User Story 1 - Administrar parqueaderos (Priority: P1) 🎯 MVP

**Goal**: CRUD de parqueaderos con nombre único por ciudad, filtros y bloqueo de eliminación con dependencias (FR-001–FR-004)

**Independent Test**: quickstart.md Escenario 1 — crear "Sede Norte", listarlo filtrando por ciudad, editar su horario, eliminarlo; duplicado → 409, campo faltante → 400

### Implementation for User Story 1

- [X] T038 [P] [US1] Crear la migración `PS-RES/db/migration/V1__crear_parqueaderos.sql`: tabla `parqueaderos` con `id uuid PK`, `nombre varchar(100) NOT NULL`, `direccion varchar(200) NOT NULL`, `ciudad varchar(80) NOT NULL`, `abierto24h boolean NOT NULL DEFAULT false`, `hora_apertura time NULL`, `hora_cierre time NULL`, `estado varchar(10) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO','INACTIVO'))`, `creado_en`/`actualizado_en timestamptz NOT NULL`; `CHECK (abierto24h OR (hora_apertura IS NOT NULL AND hora_cierre IS NOT NULL AND hora_apertura <> hora_cierre))`; índice único `uq_parqueadero_nombre_ciudad` sobre `(lower(nombre), lower(ciudad))`; índices sobre `ciudad` y `estado`
- [X] T039 [P] [US1] Crear la entidad JPA `PS/domain/Parqueadero.java` (id `UUID` generado, campos de T038, `estado` como `@Enumerated(STRING)`, timestamps con `@PrePersist`/`@PreUpdate`)
- [X] T040 [P] [US1] Crear DTOs en `PS/dto/`: `ParqueaderoRequest` (record: `nombre` `@NotBlank @Size(min=3,max=100)`, `direccion` `@NotBlank @Size(min=5,max=200)`, `ciudad` `@NotBlank @Size(min=2,max=80)`, `abierto24h` `@NotNull`, `horaApertura`/`horaCierre` `LocalTime` con `@JsonFormat(pattern="HH:mm")`, y `@AssertTrue horarioValido()` = "obligatoria si `abierto24h = false`; distinta de `horaApertura`"; una hora de cierre menor que la de apertura es válida y significa que cierra al día siguiente), `ParqueaderoUpdateRequest` (mismos campos + `estado` `@NotNull`) y `ParqueaderoResponse` (todos los campos del contrato incluido `totalEspacios`)
- [X] T041 [US1] Crear `PS/repository/ParqueaderoRepository.java` (`JpaRepository<Parqueadero, UUID>`, `JpaSpecificationExecutor`) con `existsByNombreIgnoreCaseAndCiudadIgnoreCase` y `existsByNombreIgnoreCaseAndCiudadIgnoreCaseAndIdNot`
- [X] T042 [US1] Implementar `PS/service/ParqueaderoService.java` (`@Transactional`, validaciones antes de guardar, FR-020): `crear` (trim de textos; duplicado → `ConflictoException("Ya existe un parqueadero llamado '{nombre}' en {ciudad}")`; si `abierto24h` anula las horas; estado inicial `ACTIVO`), `obtener` (404 `"El parqueadero {id} no existe"`), `listar(ciudad, estado, Paginacion)` con `Specification` (ciudad sin distinguir mayúsculas), `actualizar` (duplicado excluyendo el propio id → 409), `eliminar` (llama `VehiculosClient.contarPorParqueadero`; si > 0 → `ConflictoException("El parqueadero tiene {n} vehículos asociados; márquelo como INACTIVO")`); `totalEspacios` = 0 hasta US2
- [X] T043 [US1] Implementar `PS/controller/ParqueaderoController.java` en `/api/v1/parqueaderos`: `GET` (params `ciudad`, `estado`, `pagina`, `tamano` → `PaginaResponse`), `POST` (201 + `Location`), `GET /{parqueaderoId}`, `PUT /{parqueaderoId}`, `DELETE /{parqueaderoId}` (204), según `contracts/parqueaderos-service.openapi.yaml`
- [X] T044 [US1] Implementar `PS/controller/InternalParqueaderoController.java` con `GET /internal/v1/parqueaderos/{parqueaderoId}` (reutiliza `ParqueaderoService.obtener`)
- [X] T045 [P] [US1] Crear `FE/features/parqueaderos/parqueadero.service.ts` (`listar(filtros, pagina, tamano)`, `obtener`, `crear`, `actualizar`, `eliminar`) y el modelo `FE/features/parqueaderos/parqueadero.model.ts`
- [X] T046 [US1] Crear `FE/features/parqueaderos/parqueaderos-list/parqueaderos-list.component.ts` (+ `.html`/`.scss`): tabla con nombre, ciudad, horario ("24 h", "HH:mm–HH:mm", o "HH:mm–HH:mm (+1 día)" si cierra al día siguiente), estado, total de espacios; filtros ciudad/estado; paginación; acciones ver, editar y eliminar con confirmación; notificación de éxito
- [X] T047 [US1] Crear `FE/features/parqueaderos/parqueadero-form/parqueadero-form.component.ts` (+ `.html`): Reactive Form para crear/editar con las mismas reglas de T040 (horas visibles y obligatorias solo si no es 24 h; estado solo al editar); muestra `detalles[]` del servidor junto al campo correspondiente
- [X] T048 [US1] Crear `FE/features/parqueaderos/parqueadero-detail/parqueadero-detail.component.ts` (+ `.html`) con los datos del parqueadero (secciones de espacios y vehículos se agregan en US2/US3) y registrar rutas `''`, `nuevo`, `:id`, `:id/editar` en `FE/features/parqueaderos/parqueaderos.routes.ts`

**Checkpoint**: Escenario 1 del quickstart pasa vía gateway y desde la UI

---

## Phase 4: User Story 2 - Administrar espacios de un parqueadero (Priority: P2)

**Goal**: CRUD de espacios por parqueadero, estados controlados y resumen de disponibilidad (FR-005–FR-010)

**Independent Test**: quickstart.md Escenario 2 — crear "A-01" y "M-01", ver resumen con 2 disponibles, código duplicado → 409, poner `OCUPADO` manualmente → 400, eliminar el parqueadero con espacios → 409

### Implementation for User Story 2

- [X] T049 [P] [US2] Crear la migración `PS-RES/db/migration/V2__crear_espacios.sql`: tabla `espacios` con `id uuid PK`, `parqueadero_id uuid NOT NULL REFERENCES parqueaderos(id)`, `codigo varchar(20) NOT NULL`, `tipo_vehiculo varchar(20) NOT NULL CHECK (tipo_vehiculo IN ('AUTOMOVIL','MOTOCICLETA','BICICLETA'))`, `estado varchar(20) NOT NULL DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE','OCUPADO','FUERA_DE_SERVICIO'))`, `vehiculo_id uuid NULL`, timestamps; `CHECK ((estado = 'OCUPADO') = (vehiculo_id IS NOT NULL))`; índice único `(parqueadero_id, codigo)`; índice `(parqueadero_id, estado, tipo_vehiculo)`; índice parcial **no único** sobre `vehiculo_id WHERE vehiculo_id IS NOT NULL` (al reasignar, el vehículo ocupa el nuevo espacio antes de liberar el anterior, R2)
- [X] T050 [P] [US2] Crear la entidad JPA `PS/domain/Espacio.java` (`@ManyToOne(fetch=LAZY)` a `Parqueadero` + columna `parqueadero_id` de solo lectura para consultas, `codigo` guardado en mayúsculas y sin espacios al inicio/fin, enums `STRING`, `vehiculoId` nullable, timestamps)
- [X] T051 [P] [US2] Crear DTOs en `PS/dto/`: `EspacioRequest` (`codigo` `@NotBlank @Size(min=1,max=20)`, `tipoVehiculo` `@NotNull`), `EspacioUpdateRequest` (+ `estado` `@NotNull`; solo `DISPONIBLE` o `FUERA_DE_SERVICIO`, si llega `OCUPADO` → 400 `"El estado OCUPADO es gestionado por el sistema"`), `EspacioResponse`, `ConteoEspacios(total, disponibles, ocupados, fueraDeServicio)` y `ResumenResponse(parqueaderoId, general, Map<TipoVehiculo, ConteoEspacios> porTipo)`
- [X] T052 [US2] Crear `PS/repository/EspacioRepository.java`: `JpaSpecificationExecutor` para listar por parqueadero con filtros `estado`/`tipoVehiculo`, `existsByParqueaderoIdAndCodigo`, `existsByParqueaderoIdAndCodigoAndIdNot`, `countByParqueaderoId`, consulta JPQL de resumen agrupada por `tipoVehiculo, estado` que devuelve una proyección `(tipoVehiculo, estado, cantidad)`
- [X] T053 [US2] Implementar `PS/service/EspacioService.java`: `crear(parqueaderoId, req)` (parqueadero inexistente → 404; código normalizado a mayúsculas; duplicado → `ConflictoException("Ya existe el espacio '{codigo}' en este parqueadero")`; estado `DISPONIBLE`), `obtener`, `listar(parqueaderoId, estado, tipo, Paginacion)`, `actualizar` (si está `OCUPADO` no se puede cambiar `codigo`, `tipoVehiculo` ni `estado` → `ConflictoException("El espacio está ocupado por un vehículo")`; duplicado → 409), `eliminar` (`OCUPADO` → `ConflictoException("No se puede eliminar un espacio ocupado")`, FR-008), `resumen(parqueaderoId)` (404 si no existe; incluye los tres tipos aunque tengan 0 y totales generales)
- [X] T054 [US2] Actualizar `PS/service/ParqueaderoService.java`: `eliminar` verifica primero `countByParqueaderoId > 0` → `ConflictoException("El parqueadero tiene {n} espacios; márquelo como INACTIVO")` antes de consultar vehículos; `totalEspacios` de las respuestas se calcula con `countByParqueaderoId`
- [X] T055 [US2] Implementar `PS/controller/EspacioController.java`: `GET`/`POST /api/v1/parqueaderos/{parqueaderoId}/espacios`, `GET`/`PUT`/`DELETE /api/v1/espacios/{espacioId}` y `GET /api/v1/parqueaderos/{parqueaderoId}/resumen`, según el contrato
- [X] T056 [P] [US2] Crear `FE/features/espacios/espacio.service.ts` (`listar(parqueaderoId, filtros, pagina, tamano)`, `crear`, `actualizar`, `eliminar`, `resumen`) y `FE/features/espacios/espacio.model.ts`
- [X] T057 [US2] Crear `FE/features/espacios/espacios-panel/espacios-panel.component.ts` (+ `.html`/`.scss`, input `parqueaderoId`): tabla con código, tipo, estado; filtros estado/tipo; formulario en línea para crear; alternar `DISPONIBLE ↔ FUERA_DE_SERVICIO`; eliminar con confirmación; editar/eliminar deshabilitados si `OCUPADO`; emite evento `cambio` tras cada operación
- [X] T058 [US2] Crear `FE/features/espacios/resumen-espacios/resumen-espacios.component.ts` (+ `.html`): tabla por tipo de vehículo con total/disponibles/ocupados/fuera de servicio y fila de totales; e integrar `resumen-espacios` y `espacios-panel` en `FE/features/parqueaderos/parqueadero-detail/parqueadero-detail.component.html`, recargando el resumen en cada evento `cambio`

**Checkpoint**: Escenarios 1 y 2 del quickstart pasan

---

## Phase 5: User Story 3 - Registrar vehículos del parqueadero y asignarles espacio (Priority: P3)

**Goal**: CRUD de vehículos, búsqueda por placa, traslado y asignación/reasignación/liberación de espacios con consistencia entre servicios (FR-011–FR-018, research R2/R3)

**Independent Test**: quickstart.md Escenarios 3 y 4 — registrar `abc-123` (queda `ABC123`), asignar `A-01`, buscar por placa, liberar; tipo incompatible → 422; dos asignaciones simultáneas al mismo espacio → un 200 y un 409

### Implementation for User Story 3

#### parqueaderos-service (API interna de ocupación)

- [X] T059 [US3] Agregar a `PS/repository/EspacioRepository.java` las actualizaciones condicionales `@Modifying`: `int ocupar(id, vehiculoId)` = `UPDATE Espacio SET estado=OCUPADO, vehiculoId=:vehiculoId, actualizadoEn=now WHERE id=:id AND estado=DISPONIBLE` y `int liberar(id, vehiculoId)` = `UPDATE ... SET estado=DISPONIBLE, vehiculoId=null WHERE id=:id AND vehiculoId=:vehiculoId`
- [X] T060 [US3] Implementar `PS/service/OcupacionService.java` (`@Transactional`) y el DTO `PS/dto/OcupacionRequest.java` (`vehiculoId`, `parqueaderoId`, `tipoVehiculo`, todos `@NotNull`): `ocupar` valida en orden — espacio inexistente → 404; `espacio.parqueaderoId != parqueaderoId` → `ReglaNegocioException("El espacio no pertenece al parqueadero del vehículo")`; parqueadero `INACTIVO` → 422 `"El parqueadero está inactivo"` (FR-010); tipo distinto → 422 `"El espacio admite {tipo} y el vehículo es {tipo}"`; ya ocupado por el mismo `vehiculoId` → devuelve el espacio (idempotente); `FUERA_DE_SERVICIO` u ocupado por otro → 409 `"El espacio no está disponible"`; `ocupar(...) == 0` → 409 `"El espacio no está disponible"`. `liberar(espacioId, vehiculoId)`: inexistente → 404; ya `DISPONIBLE` → OK; ocupado por otro vehículo → 409; si no, `liberar(...)`
- [X] T061 [US3] Implementar `PS/controller/InternalEspacioController.java`: `POST /internal/v1/espacios/{espacioId}/ocupacion` (200 con `EspacioResponse`) y `DELETE /internal/v1/espacios/{espacioId}/ocupacion?vehiculoId=` (204)
- [X] T062 [US3] Implementar la liberación forzada de espacios huérfanos (espacio `OCUPADO` cuyo vehículo ya no existe o ya no lo referencia, por un fallo parcial de la saga): agregar `Optional<VehiculoRef(id, placa, espacioId)> obtenerVehiculo(UUID)` a `PS/client/VehiculosClient.java` (`GET /internal/v1/vehiculos/{vehiculoId}`, 404 → vacío, error/timeout → `ServicioNoDisponibleException`); agregar `forzarLiberacion(espacioId)` a `PS/service/OcupacionService.java`: espacio inexistente → 404; `DISPONIBLE`/`FUERA_DE_SERVICIO` → devuelve el espacio sin cambios; si el vehículo existe y su `espacioId` es este espacio → `ConflictoException("El espacio está asignado al vehículo {placa}; libérelo desde el vehículo")`; si no, `liberar(id, vehiculoId)` y log `WARN` `ESPACIO_HUERFANO_LIBERADO`; exponerlo como `DELETE /api/v1/espacios/{espacioId}/ocupacion` (200 con `EspacioResponse`) en `PS/controller/EspacioController.java`. El sistema verifica antes de liberar, así que se mantiene FR-007

#### vehiculos-service

- [X] T063 [P] [US3] Crear `VS/internal/vehiculo/model.go`: `TipoVehiculo` (`AUTOMOVIL`, `MOTOCICLETA`, `BICICLETA`), struct `Vehiculo` con tags JSON del contrato (`id, placa, tipo, marca, color, propietarioNombre, propietarioDocumento, parqueaderoId, espacioId, espacioCodigo, creadoEn, actualizadoEn`), `VehiculoRequest`, `NormalizarPlaca(s)` (mayúsculas, sin espacios ni guiones) y `Validar(req)` que acumula detalles: placa normalizada `^[A-Z0-9]{3,10}$`, `tipo` en el enum, `marca` "obligatorio salvo `BICICLETA` (opcional)" y máx. 50, `color` 1–30, `propietarioNombre` 3–100, `propietarioDocumento` 5–20 → `ErrValidacion`
- [X] T064 [P] [US3] Implementar `VS/internal/parqueaderos/client.go`: `Client` con `http.Client{Timeout: PARQUEADEROS_TIMEOUT}` que reenvía `X-Correlation-Id`; métodos `ObtenerParqueadero(ctx, id)` (404 → `ErrNoEncontrado("El parqueadero {id} no existe")`), `OcuparEspacio(ctx, espacioID, vehiculoID, parqueaderoID, tipo) (EspacioRef{ID, Codigo}, error)` y `LiberarEspacio(ctx, espacioID, vehiculoID)`; 404/409/422 remotos → error de dominio con el mismo código y `mensaje` remoto; red/timeout/5xx → `ErrServicioNoDisponible("El servicio de parqueaderos no está disponible")`
- [X] T065 [US3] Completar `VS/internal/vehiculo/repository.go`: definir la interfaz `Repositorio` y en `PgRepository` implementar `Crear`, `ObtenerPorID` (no existe → `ErrNoEncontrado("El vehículo {id} no existe")`), `Listar(ctx, parqueaderoID *uuid, placa *string, pagina, tamano) ([]Vehiculo, total int64, error)` ordenado por placa (filtro `placa LIKE $prefijo || '%'`; ambos filtros combinables), `Actualizar`, `ActualizarEspacio(ctx, id, espacioID *uuid, codigo *string)`, `ActualizarParqueadero(ctx, id, parqueaderoID)` (limpia espacio), `Eliminar`; violación única `23505` sobre `placa` → `ErrConflicto("La placa {placa} ya está registrada")` y sobre `espacio_id` → `ErrConflicto("El espacio ya está asignado a otro vehículo")`
- [X] T066 [US3] Implementar `VS/internal/vehiculo/service.go` con dependencias por interfaz (`Repositorio`, `ParqueaderosClient`) y validación previa a cualquier escritura: `Registrar` (valida + normaliza; verifica parqueadero remoto: inexistente → `ErrNoEncontrado`, `INACTIVO` → `ErrReglaNegocio("El parqueadero está inactivo")` (FR-013a); inserta), `Obtener`, `Listar` (exige `parqueaderoId` o `placa`, si no → `ErrValidacion`; normaliza placa; placa normalizada con menos de 2 caracteres → `ErrValidacion("La búsqueda por placa requiere al menos 2 caracteres")`; búsqueda por prefijo, FR-018), `Actualizar` (no cambia parqueadero ni espacio; cambio de `tipo` con espacio asignado → `ErrConflicto("No se puede cambiar el tipo de un vehículo con espacio asignado")`), `Eliminar` (dentro de una transacción pgx: borra la fila sin confirmar; si tiene espacio llama `LiberarEspacio` remoto; si el remoto falla → rollback y `ErrServicioNoDisponible`; si no, commit; FR-017), `Trasladar` (parqueadero destino existe y está `ACTIVO`, si no → `ErrReglaNegocio("El parqueadero está inactivo")` (FR-013a); igual al actual → `ErrReglaNegocio`; libera espacio si lo tiene; FR-013), `Asignar` (ya tiene espacio → `ErrConflicto("El vehículo ya tiene asignado el espacio {codigo}; libérelo o reasígnelo")`; `OcuparEspacio` remoto; `ActualizarEspacio`; si falla la escritura local, compensa con `LiberarEspacio` (hasta 3 intentos con espera de 200 ms, la liberación es idempotente) y devuelve el error original; si la compensación también falla, log `ERROR` con `espacioId`, `vehiculoId` y el texto `ESPACIO_HUERFANO` — el espacio se recupera con la liberación forzada de T062), `Reasignar` (sin espacio → se comporta como `Asignar`; mismo espacio → sin cambios; ocupar nuevo → actualizar → liberar anterior con hasta 3 intentos (200 ms); si los 3 fallan, log `ERROR` `ESPACIO_HUERFANO` con ambos ids y responde éxito, porque el vehículo ya quedó en el espacio nuevo y el anterior se recupera con T062), `Liberar` (sin espacio → devuelve el vehículo; `LiberarEspacio` remoto → limpia espacio)
- [X] T067 [US3] Implementar `VS/internal/httpapi/vehiculos_handlers.go` y registrarlos en `VS/internal/httpapi/router.go`: `GET`/`POST /api/v1/vehiculos`, `GET`/`PUT`/`DELETE /api/v1/vehiculos/{vehiculoId}`, `POST /api/v1/vehiculos/{vehiculoId}/traslado`, `POST`/`PUT`/`DELETE /api/v1/vehiculos/{vehiculoId}/asignacion`; UUID inválido o JSON mal formado → 400; `pagina`/`tamano` con los mismos límites que R10; respuestas según `contracts/vehiculos-service.openapi.yaml` (201 en registro); y conectar `parqueaderos.Client` y `vehiculo.Service` en `VS/cmd/server/main.go`
- [X] T068 [US3] Agregar `GET /internal/v1/vehiculos/{vehiculoId}` en `VS/internal/httpapi/internal_handlers.go` (200 con `{id, placa, espacioId}`; 404 si no existe; UUID inválido → 400), usado por la liberación forzada de T062

#### frontend

- [X] T069 [P] [US3] Crear `FE/features/vehiculos/vehiculo.service.ts` (`listar({parqueaderoId?, placa?}, pagina, tamano)`, `obtener`, `registrar`, `actualizar`, `eliminar`, `trasladar`, `asignar`, `reasignar`, `liberar`) y `FE/features/vehiculos/vehiculo.model.ts`
- [X] T070 [US3] Crear `FE/features/vehiculos/vehiculos-list/vehiculos-list.component.ts` (+ `.html`/`.scss`): búsqueda por placa y filtro por parqueadero (select cargado desde `ParqueaderoService`), tabla con placa, tipo, marca, color, propietario, espacio (`espacioCodigo` o "Sin espacio"), acciones editar, asignar espacio y eliminar; registrar rutas `''`, `nuevo`, `:id/editar` en `FE/features/vehiculos/vehiculos.routes.ts`
- [X] T071 [US3] Crear `FE/features/vehiculos/vehiculo-form/vehiculo-form.component.ts` (+ `.html`): Reactive Form con reglas de T063 (marca opcional si `BICICLETA`), select de parqueadero solo al registrar, `tipo` deshabilitado si tiene espacio; muestra `detalles[]` del servidor por campo
- [X] T072 [US3] Crear `FE/features/vehiculos/asignacion-espacio/asignacion-espacio.component.ts` (+ `.html`): lista espacios `DISPONIBLE` del parqueadero del vehículo filtrados por su tipo (vía `EspacioService.listar`), acciones asignar (si no tiene) / reasignar (si tiene) / liberar, y sección de traslado a otro parqueadero; tras cada operación refresca y notifica
- [X] T073 [US3] Agregar la sección "Vehículos" a `FE/features/parqueaderos/parqueadero-detail/parqueadero-detail.component.html` (lista paginada vía `VehiculoService.listar({parqueaderoId})` con su espacio y enlace a asignación) y recargar el resumen de espacios tras asignar/liberar
- [X] T074 [US3] Agregar `forzarLiberacion(espacioId)` a `FE/features/espacios/espacio.service.ts` y, en `FE/features/espacios/espacios-panel/espacios-panel.component.ts` (+ `.html`), una acción "Forzar liberación" visible solo en espacios `OCUPADO`, con confirmación que explica que solo funciona si ningún vehículo tiene asignado el espacio; muestra el mensaje 409 del servidor si el espacio sí está asignado

**Checkpoint**: Escenarios 1–5 del quickstart pasan; las tres historias funcionan

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Pruebas recomendadas (Principio V), documentación y validación final

- [X] T075 [P] (Recomendado) Prueba de integración `services/parqueaderos-service/src/test/java/co/edu/usbcali/parqueaderos/service/OcupacionServiceIT.java` con Testcontainers PostgreSQL: dos hilos ocupan el mismo espacio a la vez → exactamente un éxito y un `ConflictoException`; tipo incompatible → 422; liberar es idempotente — *Nota de implementación: la prueba usa `@Testcontainers(disabledWithoutDocker = true)`; en el entorno de implementación no había Docker y se omitió. Los mismos casos se verificaron por HTTP contra PostgreSQL real.*
- [X] T076 [P] (Recomendado) Prueba unitaria `services/parqueaderos-service/src/test/java/co/edu/usbcali/parqueaderos/service/ParqueaderoServiceTest.java` (Mockito): nombre duplicado en la misma ciudad → 409; eliminar con espacios → 409; con vehículos → 409; `VehiculosClient` caído → 503 y no se elimina
- [X] T077 [P] (Recomendado) Pruebas Go en `VS/internal/vehiculo/model_test.go` (tabla de casos para `NormalizarPlaca` y `Validar`) y `VS/internal/vehiculo/service_test.go` con repositorio y cliente falsos: `Asignar` compensa con `LiberarEspacio` si falla la escritura local; `Asignar` con espacio ya asignado → conflicto; `Eliminar` libera el espacio
- [X] T078 [P] Crear `README.md` raíz con la arquitectura (diagrama gateway → servicios → BDs), puertos, cómo levantar con `docker compose`, variables de `.env.example` y enlace a `specs/001-gestion-parqueaderos/quickstart.md`, más una advertencia visible: esta versión no tiene autenticación y no debe exponerse a internet
- [X] T079 Verificar observabilidad: el mismo `X-Correlation-Id` aparece en los logs JSON de gateway, vehiculos-service y parqueaderos-service para una asignación; ajustar `GW/`, `PS/config/` o `VS/internal/httpapi/middleware.go` si falta
- [X] T080 Ejecutar de punta a punta `specs/001-gestion-parqueaderos/quickstart.md` (Escenarios 1–5 y flujo de UI) y corregir cualquier diferencia con los contratos — *Nota de implementación: escenarios 1–5 ejecutados contra los servicios como procesos locales (28/28 verificaciones); sin Docker no se probó `docker compose up` ni las imágenes. El flujo de UI se verificó con `ng build`, `ng test` y `ng serve` + proxy, no haciendo clic en un navegador.*

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: sin dependencias. T010 depende de T006–T009.
- **Foundational (Phase 2)**: depende de Setup — BLOQUEA todas las historias. Incluye el endpoint
  interno de conteo de vehículos (T027–T028) porque US1 lo necesita para eliminar parqueaderos.
- **US1 (Phase 3)**: depende de Foundational.
- **US2 (Phase 4)**: depende de US1 (los espacios pertenecen a un parqueadero; T054 modifica
  `ParqueaderoService`).
- **US3 (Phase 5)**: depende de US1 (parqueadero del vehículo) y US2 (espacios a asignar). El
  backend Go (T063–T068) puede avanzar en paralelo con US2 usando un cliente falso.
- **Polish (Phase 6)**: depende de las historias que se entreguen.

### Within Each User Story

- Migración / entidad / DTO → repositorio → servicio → controlador → frontend (servicio → componentes).
- En US3: API interna de ocupación (T059–T061) antes de probar la asignación de extremo a extremo; la liberación forzada (T062) necesita el endpoint Go T068, y su botón en la UI (T074) necesita T062.

### Parallel Opportunities

- Setup: T002–T009, T011 y T012 en paralelo.
- Foundational: los cuatro bloques (parqueaderos-service, vehiculos-service, gateway, frontend)
  avanzan en paralelo; dentro de cada uno, las tareas `[P]`.
- US1: T038, T039, T040 y T045 en paralelo.
- US2: T049, T050, T051 y T056 en paralelo.
- US3: T063, T064 y T069 en paralelo; el bloque Go completo en paralelo con T059–T061.
- Polish: T075–T078 en paralelo.

---

## Parallel Example: User Story 3

```bash
# Backend Java y Go a la vez:
Task: "T059–T061 API interna de ocupación en services/parqueaderos-service"
Task: "T063 model.go en services/vehiculos-service/internal/vehiculo/"
Task: "T064 client.go en services/vehiculos-service/internal/parqueaderos/"
Task: "T069 vehiculo.service.ts en frontend/src/app/features/vehiculos/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (Setup) y Phase 2 (Foundational).
2. Completar Phase 3 (US1).
3. **PARAR y VALIDAR**: Escenario 1 del quickstart y UI de parqueaderos.
4. Demostrar: arquitectura completa (gateway + 2 servicios + frontend en contenedores) con un CRUD real.

### Incremental Delivery

1. Setup + Foundational → todo levanta con `docker compose up`.
2. US1 → validar → demo (MVP).
3. US2 → validar Escenario 2 → demo.
4. US3 → validar Escenarios 3–5 → demo.
5. Polish → pruebas recomendadas, README, validación final.

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Las pruebas son recomendadas (Principio V); no bloquean el avance entre historias
- Commit después de cada tarea o grupo lógico
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
