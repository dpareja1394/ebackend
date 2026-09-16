# Bitácora de cambios

## Fecha: 2026-09-15

### Cambios registrados en el repositorio

- Se creó la documentación formal del proyecto en `Servicios/documentacion/`.
- Se agregó la documentación técnica del proyecto: `doc-tecnica-servicios.md`.
- Se agregó la guía de onboarding para el equipo: `doc-onboarding-equipo-servicios.md`.
- Se creó el índice de documentación: `Servicios/documentacion/README.md`.
- Se creó el README del proyecto en `Servicios/README.md` con referencias a requisitos, esquema SQL, documentación y microservicio.
- Se añadieron referencias cruzadas a la base de datos y a los scripts `schema.sql`, `create-db.sql` y `seed.sql`.
- Se documentó el alcance del sistema de concesionaria y su estructura funcional.

### Trabajo realizado durante la jornada

- Se revisó el proyecto de servicios desde la lista de requisitos funcionales hasta el modelado inicial relacional.
- Se validó la estructura de la carpeta `Servicios` y la relación entre negocio, scripts SQL y microservicio.
- Se organizó la documentación para uso académico y para revisión en GitHub.
- Se dejó una base clara para continuar la implementación del backend en Spring Boot.

### Estado al cierre

La documentación del proyecto Servicios quedó organizada y útil para revisión, onboarding y continuidad del desarrollo. La carpeta mantiene trazabilidad del negocio, del diseño del modelo y de la arquitectura propuesta.

---

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
