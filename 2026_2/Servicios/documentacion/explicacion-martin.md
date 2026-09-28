# La capa de Servicio, explicada para Martín 🚗

Hola Martín, esta es la versión corta con dibujitos. Si quieres todo el detalle y el código completo, está en [doc-capa-servicio.md](./doc-capa-servicio.md).

## La idea en una frase

> El **Service** es el que piensa. El **Controller** solo recibe y responde. El **Repository** solo guarda y consulta.

## El viaje de una petición

```text
📱 Cliente (Postman / Frontend)
        │
        │  GET /api/roles/por-id/3
        ▼
┌───────────────────┐
│   RolController    │   "Llegó una petición, se la paso al Service"
└─────────┬──────────┘
          ▼
┌───────────────────┐
│    RolService      │   "Busco el rol, y si existe lo convierto a DTO"
└─────────┬──────────┘
          ▼
┌───────────────────┐
│  RolRepository      │   "Voy a la base de datos y traigo el registro"
└─────────┬──────────┘
          ▼
      🗄️ PostgreSQL
```

Hoy el `Controller` se salta al `Service` y habla directo con el `Repository`. Lo que vamos a hacer es meter al `Service` en la mitad.

## Antes vs. después (con ejemplo real)

**Antes** — el Controller hace de todo:

```java
@GetMapping
public List<ObtenerRolResponse> obtenerTodos() {
    return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
}
```

**Después** — el Controller solo delega:

```java
@GetMapping
public List<ObtenerRolResponse> obtenerTodos() {
    return rolService.obtenerTodos();
}
```

Y el que quedó con el trabajo pesado (buscar + convertir con el Mapper) es el `RolService`:

```java
public List<ObtenerRolResponse> obtenerTodos() {
    return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
}
```

👉 Literalmente moviste la línea de código de un archivo a otro. Eso es todo lo que pasa con los `GET` que ya existen.

## Lo nuevo: crear, actualizar, eliminar

Para estas tres operaciones vamos a usar un DTO de entrada ("Request"), así el cliente nos manda solo lo necesario:

```text
CrearRolRequest        →  { "nombre": "ADMIN", "descripcion": "...", "activo": true }
ActualizarRolRequest   →  { "nombre": "ADMIN", "descripcion": "...", "activo": false }
EliminarRolRequest     →  { "id": 3 }
```

Diagrama de "crear un rol":

```text
📱 POST /api/roles  { nombre: "ADMIN" }
        │
        ▼
RolController.crear(CrearRolRequest)
        │  "no valido nada, solo paso el paquete"
        ▼
RolService.crear(request)
        │  "armo la entidad Rol, la guardo, la devuelvo como DTO"
        ▼
RolRepository.save(rol)  →  🗄️ INSERT INTO roles...
```

## Regla de oro para no perderte

| Capa | ¿Con qué habla? | ¿Qué NO debe hacer? |
|---|---|---|
| **Controller** | Solo con el `Service` | No debe usar el `Repository` ni el `Mapper` directamente |
| **Service** | `Repository` + `Mapper` | No debe armar `ResponseEntity` ni saber de HTTP |
| **Repository** | Base de datos | No debe tener reglas de negocio |

## Receta corta para crear un Service nuevo

1. Paquete `service`, clase `XxxService`.
2. `@Service` + `@AllArgsConstructor` (igual que ya haces en los controllers).
3. Inyectas el `Repository`.
4. Mueves los métodos `GET` del Controller al Service, tal cual.
5. Para `crear` / `actualizar` / `eliminar`, creas los DTO Request y les agregas `@Transactional` a esos métodos.

¡Eso es prácticamente todo, Martín! Si quieres, seguimos con el código completo del `RolService` en el documento largo.
