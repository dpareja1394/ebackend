# Contrato: API Gateway

Punto único de entrada para el frontend y clientes externos (Principio II). Puerto **8080**.

## Rutas

| Orden | Predicado (Path) | Destino | Notas |
|-------|------------------|---------|-------|
| 1 | `/api/v1/vehiculos`, `/api/v1/vehiculos/**` | `http://vehiculos-service:8082` | Go |
| 2 | `/api/v1/parqueaderos/**`, `/api/v1/parqueaderos`, `/api/v1/espacios/**` | `http://parqueaderos-service:8081` | Spring Boot |
| — | `/internal/**` | **ninguno** → `404` | API interna nunca se expone |

Las rutas conservan el path original (sin `StripPrefix`). URLs de destino configurables por
variables de entorno: `PARQUEADEROS_URL`, `VEHICULOS_URL`.

## Filtros globales

- **Correlation id**: si la petición no trae `X-Correlation-Id`, el gateway genera un UUID; lo
  reenvía al servicio y lo devuelve en la respuesta.
- **CORS**: el frontend no lo necesita, porque su Nginx (y `ng serve` con `proxy.conf.json`)
  reenvía `/api/` al gateway desde el mismo origen. Se deja configurado para otros clientes de
  navegador: origen `FRONTEND_ORIGIN`, métodos `GET, POST, PUT, DELETE, OPTIONS`.
- **Timeout**: respuesta del servicio en máx. 5 s; si se excede o el servicio no está
  disponible, responde `503`/`504` con el formato de error estándar (`SERVICIO_NO_DISPONIBLE`).

## Exposición de puertos

Solo el gateway (8080) y el frontend (4200) publican puertos en el host. Los servicios y las bases
de datos solo son alcanzables dentro de la red de Docker. Para desarrollo, `docker-compose.dev.yml`
publica 8081/8082 (Swagger) y los puertos de las bases.

## Salud

- `GET /actuator/health` del gateway, usado por Docker Compose.
