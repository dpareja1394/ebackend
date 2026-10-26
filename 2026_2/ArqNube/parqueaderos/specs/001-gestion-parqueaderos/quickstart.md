# Quickstart: validación de Gestión de Parqueaderos

Guía para levantar el sistema y comprobar de punta a punta la funcionalidad
`001-gestion-parqueaderos`. Contratos: [contracts/](./contracts/). Reglas: [data-model.md](./data-model.md).

## Prerrequisitos

- Docker y Docker Compose v2.
- `curl` y `jq` (para los escenarios por línea de comandos).
- Opcional para desarrollo fuera de contenedores: JDK 21, Go 1.26, Node.js 22 + Angular CLI 20.

## Levantar el sistema

```bash
cp .env.example .env          # credenciales de las bases y URLs internas
docker compose up --build -d
docker compose ps             # todos los servicios deben quedar "healthy"
```

| Componente | URL |
|------------|-----|
| Frontend | http://localhost:4200 (reenvía `/api/` al gateway) |
| API Gateway | http://localhost:8080 |

Solo el gateway y el frontend publican puertos. Para tener Swagger
(http://localhost:8081/swagger-ui.html) y acceso directo a los servicios y las bases, levantar en
modo desarrollo:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
```

Todas las llamadas de abajo van **al gateway**.

```bash
API=http://localhost:8080/api/v1
```

## Escenario 1 — Parqueaderos (US1)

```bash
P=$(curl -s -X POST $API/parqueaderos -H 'Content-Type: application/json' \
  -d '{"nombre":"Sede Norte","direccion":"Cra 1 # 2-3","ciudad":"Cali","abierto24h":false,"horaApertura":"06:00","horaCierre":"22:00"}' | jq -r .id)
curl -s "$API/parqueaderos?ciudad=Cali" | jq '.totalElementos'        # ≥ 1
```

Esperado:
- Repetir el POST con el mismo nombre y ciudad → `409 CONFLICTO`.
- POST sin `direccion` → `400 VALIDACION` con `detalles[].campo = "direccion"`.

## Escenario 2 — Espacios y resumen (US2)

```bash
E1=$(curl -s -X POST $API/parqueaderos/$P/espacios -H 'Content-Type: application/json' \
  -d '{"codigo":"A-01","tipoVehiculo":"AUTOMOVIL"}' | jq -r .id)
E2=$(curl -s -X POST $API/parqueaderos/$P/espacios -H 'Content-Type: application/json' \
  -d '{"codigo":"M-01","tipoVehiculo":"MOTOCICLETA"}' | jq -r .id)
curl -s $API/parqueaderos/$P/resumen | jq
```

Esperado:
- Resumen: `general.total = 2`, `general.disponibles = 2`.
- Crear otra vez `A-01` en el mismo parqueadero → `409`.
- `PUT $API/espacios/$E2` con `"estado":"OCUPADO"` → `400` (estado reservado al sistema).
- Eliminar el parqueadero ahora → `409` (tiene espacios).

## Escenario 3 — Vehículos y asignación (US3)

```bash
V=$(curl -s -X POST $API/vehiculos -H 'Content-Type: application/json' \
  -d "{\"parqueaderoId\":\"$P\",\"placa\":\"abc-123\",\"tipo\":\"AUTOMOVIL\",\"marca\":\"Mazda\",\"color\":\"Rojo\",\"propietarioNombre\":\"Ana Pérez\",\"propietarioDocumento\":\"1144000000\"}" | jq -r .id)
curl -s -X POST $API/vehiculos/$V/asignacion -H 'Content-Type: application/json' -d "{\"espacioId\":\"$E1\"}" | jq '.espacioCodigo'  # "A-01"
curl -s "$API/vehiculos?placa=abc-123" | jq '.contenido[0].espacioCodigo'                                                       # "A-01"
curl -s $API/espacios/$E1 | jq -r .estado                                                                                          # OCUPADO
```

Esperado:
- La placa queda guardada como `ABC123`; registrar `ABC123` de nuevo → `409`.
- Asignar `$E2` (motocicleta) al vehículo → `409` si ya tiene espacio; tras liberar → `422`
  (tipo incompatible).
- `DELETE $API/vehiculos/$V/asignacion` → espacio `$E1` vuelve a `DISPONIBLE`.
- `DELETE $API/espacios/$E1` mientras está ocupado → `409`.
- Eliminar el vehículo con espacio asignado → el espacio queda `DISPONIBLE`.

## Escenario 4 — Concurrencia (edge case)

Con dos vehículos `V1`, `V2` del mismo tipo y un espacio libre `E`:

```bash
curl -s -o /dev/null -w '%{http_code}\n' -X POST $API/vehiculos/$V1/asignacion -H 'Content-Type: application/json' -d "{\"espacioId\":\"$E\"}" &
curl -s -o /dev/null -w '%{http_code}\n' -X POST $API/vehiculos/$V2/asignacion -H 'Content-Type: application/json' -d "{\"espacioId\":\"$E\"}" &
wait
```

Esperado: exactamente un `200` y un `409`.

## Escenario 5 — Resiliencia y gateway

- `curl -i http://localhost:8080/internal/v1/vehiculos/conteo?parqueaderoId=$P` → `404`
  (la API interna no se expone).
- Sin `docker-compose.dev.yml`, `curl http://localhost:8081/actuator/health` falla (conexión
  rechazada): los servicios no se publican en el host.
- `DELETE $API/espacios/$E1/ocupacion` sobre un espacio asignado a un vehículo → `409`; sobre un
  espacio libre → `200` sin cambios.
- Toda respuesta trae el encabezado `X-Correlation-Id`, y el mismo id aparece en los logs JSON de
  los servicios involucrados (`docker compose logs vehiculos-service parqueaderos-service`).
- `docker compose stop vehiculos-service` y eliminar un parqueadero sin espacios → `503`
  `SERVICIO_NO_DISPONIBLE` (no se elimina sin confirmar).

## Pruebas automatizadas (recomendadas)

```bash
(cd services/parqueaderos-service && ./mvnw test)
(cd services/vehiculos-service && go test ./...)
(cd frontend && npx ng test --watch=false)
```

## Frontend

En http://localhost:4200 repetir el flujo crear parqueadero → crear espacio → registrar vehículo
→ asignar espacio (SC-006) y verificar que los errores del servidor se muestran con su mensaje.
