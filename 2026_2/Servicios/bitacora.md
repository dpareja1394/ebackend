# Bitácora de cambios

## Fecha: 2026-09-07

### Cambios registrados en el repositorio

**Commit `22e51b1` - `Repository`**

- Se agregó la documentación del patrón Repository en `Servicios/explicaciones/`.
- Se incorporó la imagen explicativa del patrón Repository.
- Se ajustaron entidades del microservicio `autosusbcali` para completar su mapeo con la base de datos.
- Se creó el `UsuarioRepository`.

### Trabajo realizado durante la jornada

- Se completaron los repositorios faltantes del microservicio `Servicios/microservices/autosusbcali`.
- Se creó un repositorio Spring Data JPA para cada entidad del microservicio, utilizando `Long` como tipo de identificador.
- Se creó `RolController` con inyección por constructor de `RolRepository`.
- Se implementó el CRUD básico de roles:
  - `GET /api/roles`
  - `GET /api/roles/{id}`
  - `POST /api/roles`
  - `PUT /api/roles/{id}`
  - `DELETE /api/roles/{id}`
- Se agregó manejo de respuestas `404 Not Found` para roles inexistentes y `204 No Content` después de una eliminación exitosa.

### Estado al cierre

Los cambios de repositorios y del controlador permanecen como cambios pendientes de commit en el árbol de trabajo.
