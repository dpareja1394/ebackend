# Dnetwork Java

Microservicio backend desarrollado en Java con Spring Boot para la plataforma DNetwork. El proyecto está estructurado como una API REST con acceso a base de datos PostgreSQL, mapeo ORM con JPA/Hibernate y documentación interactiva de la API con Swagger (OpenAPI).

## Estado del proyecto

Actualmente el servicio cuenta con:

- Configuración de Spring Boot 4.1.0
- Dependencias para Web MVC y JPA
- Soporte para PostgreSQL
- Entidades JPA para el dominio principal de la red social
- Enums de dominio persistidos mediante converters JPA
- Repositorios de Spring Data
- Capa de servicios con validaciones de negocio y manejo transaccional
- CRUD completo de publicaciones (crear, consultar, modificar y eliminar)
- Consulta y creación de etiquetas
- Consulta de usuarios
- Mappers y DTOs (records) para solicitudes y respuestas
- Documentación de la API con Swagger UI (springdoc-openapi)
- Prueba inicial de arranque de la aplicación

## Stack tecnológico

- Java 25
- Maven
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- springdoc-openapi 3.1.1 (Swagger UI)

## Estructura del proyecto

```text
dnetwork-java/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/usbcali/dnetwork_java/
│   │   │       ├── controller/
│   │   │       │   ├── EtiquetaController.java
│   │   │       │   ├── PublicacionController.java
│   │   │       │   └── UsuarioController.java
│   │   │       ├── domain/
│   │   │       │   ├── ActividadHistorial.java
│   │   │       │   ├── Bloqueo.java
│   │   │       │   ├── Comentario.java
│   │   │       │   ├── Denuncia.java
│   │   │       │   ├── Etiqueta.java
│   │   │       │   ├── Medio.java
│   │   │       │   ├── Notificacion.java
│   │   │       │   ├── Perfil.java
│   │   │       │   ├── Publicacion.java
│   │   │       │   ├── PublicacionEtiqueta.java
│   │   │       │   ├── Reaccion.java
│   │   │       │   ├── Relacion.java
│   │   │       │   ├── Usuario.java
│   │   │       │   ├── converter/
│   │   │       │   │   ├── EstadoDenunciaConverter.java
│   │   │       │   │   ├── EstadoRelacionConverter.java
│   │   │       │   │   ├── PrivacidadPublicacionConverter.java
│   │   │       │   │   ├── TipoMedioConverter.java
│   │   │       │   │   ├── TipoNotificacionConverter.java
│   │   │       │   │   ├── TipoReaccionConverter.java
│   │   │       │   │   ├── ValorDbEnumConverter.java
│   │   │       │   │   └── VisibilidadPerfilConverter.java
│   │   │       │   └── enums/
│   │   │       │       ├── EstadoDenuncia.java
│   │   │       │       ├── EstadoRelacion.java
│   │   │       │       ├── IValorDbEnum.java
│   │   │       │       ├── PrivacidadPublicacion.java
│   │   │       │       ├── TipoMedio.java
│   │   │       │       ├── TipoNotificacion.java
│   │   │       │       ├── TipoReaccion.java
│   │   │       │       └── VisibilidadPerfil.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── CrearEtiquetaRequest.java
│   │   │       │   │   ├── CrearPublicacionRequest.java
│   │   │       │   │   └── ModificarPublicacionRequest.java
│   │   │       │   └── response/
│   │   │       │       ├── ObtenerEtiquetaResponse.java
│   │   │       │       ├── ObtenerPublicacionResponse.java
│   │   │       │       └── ObtenerUsuarioResponse.java
│   │   │       ├── mapper/
│   │   │       │   ├── EtiquetaMapper.java
│   │   │       │   ├── PublicacionMapper.java
│   │   │       │   └── UsuarioMapper.java
│   │   │       ├── repository/
│   │   │       │   ├── ActividadHistorialRepository.java
│   │   │       │   ├── BloqueoRepository.java
│   │   │       │   ├── ComentarioRepository.java
│   │   │       │   ├── DenunciaRepository.java
│   │   │       │   ├── EtiquetaRepository.java
│   │   │       │   ├── MedioRepository.java
│   │   │       │   ├── NotificacionRepository.java
│   │   │       │   ├── PerfilRepository.java
│   │   │       │   ├── PublicacionEtiquetaRepository.java
│   │   │       │   ├── PublicacionRepository.java
│   │   │       │   ├── ReaccionRepository.java
│   │   │       │   ├── RelacionRepository.java
│   │   │       │   └── UsuarioRepository.java
│   │   │       ├── service/
│   │   │       │   ├── EtiquetaService.java
│   │   │       │   ├── EtiquetaServiceImpl.java
│   │   │       │   ├── PublicacionService.java
│   │   │       │   └── PublicacionServiceImpl.java
│   │   │       └── DnetworkJavaApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── co/edu/usbcali/dnetwork_java/
│               └── DnetworkJavaApplicationTests.java
├── .gitignore
├── .gitattributes
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Arquitectura por capas

- `controller` → endpoints REST
- `service` → lógica de negocio, validaciones y transacciones (interfaz + implementación)
- `repository` → acceso a datos con Spring Data JPA
- `domain` → entidades del negocio
- `domain/enums` → enums del dominio; implementan `IValorDbEnum`, que expone el valor (`valorDb`) almacenado en base de datos
- `domain/converter` → converters JPA que traducen cada enum a su valor en base de datos; heredan de la clase genérica `ValorDbEnumConverter`
- `dto` → contratos de entrada (`request`) y salida (`response`), implementados como `record`
- `mapper` → conversión entre entidades y DTOs
- `resources` → configuración de la app

## Dominio

El proyecto modela una red social o plataforma de contenidos generado por usuarios, con las siguientes entidades:

- Usuarios
- Perfiles
- Etiquetas
- Publicaciones y su relación con etiquetas
- Comentarios
- Reacciones
- Relaciones entre usuarios
- Bloqueos
- Denuncias
- Notificaciones
- Actividad de historial
- Medios asociados a publicaciones

### Enums del dominio

| Enum | Uso |
|------|-----|
| `PrivacidadPublicacion` | Privacidad de una publicación: `publico`, `seguidores`, `privado` |
| `VisibilidadPerfil` | Visibilidad del perfil de un usuario |
| `EstadoRelacion` | Estado de una relación entre usuarios |
| `EstadoDenuncia` | Estado de una denuncia |
| `TipoReaccion` | Tipo de reacción a una publicación |
| `TipoMedio` | Tipo de medio asociado a una publicación |
| `TipoNotificacion` | Tipo de notificación |

## Documentación de la API (Swagger)

El proyecto incluye `springdoc-openapi-starter-webmvc-ui`, que genera automáticamente la documentación OpenAPI a partir de los controladores. Con la aplicación en ejecución (puerto por defecto `8080`):

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Especificación OpenAPI (JSON): <http://localhost:8080/v3/api-docs>

Desde Swagger UI se pueden consultar y probar todos los endpoints descritos a continuación.

## Endpoints

### `/etiquetas`

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/etiquetas/ping` | Respuesta de prueba (`pong`) |
| `GET` | `/etiquetas/validar-estado` | Validación de estado del servicio |
| `GET` | `/etiquetas/obtener-etiquetas` | Lista de etiquetas |
| `GET` | `/etiquetas/{id}` | Obtiene una etiqueta por id |
| `POST` | `/etiquetas/crear` | Crea una etiqueta (responde `201 Created`) |

Ejemplo de cuerpo para crear una etiqueta:

```json
{
  "nombre": "tecnologia"
}
```

Validaciones: el nombre no puede ser nulo ni vacío y no puede existir previamente.

### `/publicaciones`

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/publicaciones` | Lista de publicaciones |
| `GET` | `/publicaciones/{id}` | Obtiene una publicación por id |
| `POST` | `/publicaciones/crear` | Crea una publicación (responde `201 Created`) |
| `PUT` | `/publicaciones/modificar/{id}` | Modifica el contenido y la privacidad de una publicación |
| `DELETE` | `/publicaciones/eliminar/{id}` | Elimina físicamente una publicación (responde `204 No Content`) |

Ejemplo de cuerpo para crear una publicación:

```json
{
  "autor_id": 1,
  "contenido": "Mi primera publicación",
  "privacidad_publicacion": "publico"
}
```

Ejemplo de cuerpo para modificar una publicación:

```json
{
  "contenido": "Contenido actualizado",
  "privacidad_publicacion": "seguidores"
}
```

Ejemplo de respuesta:

```json
{
  "id": 1,
  "autor_username": "usuario1",
  "autor_id": 1,
  "contenido": "Mi primera publicación",
  "privacidad_publicacion": "publico",
  "created_at": "2026-10-27T10:00:00",
  "updated_at": "2026-10-27T10:00:00"
}
```

Validaciones:

- El id debe ser mayor que cero y la publicación debe existir.
- `autor_id` es obligatorio, mayor que cero y debe corresponder a un usuario existente.
- `contenido` no puede ser nulo ni vacío.
- `privacidad_publicacion` es obligatoria y debe ser uno de los valores del enum `PrivacidadPublicacion` (`publico`, `seguidores`, `privado`).

### `/usuarios`

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/usuarios/ping` | Respuesta de prueba (`pong`) |
| `GET` | `/usuarios/validar-estado` | Validación de estado del servicio |
| `GET` | `/usuarios/obtener-usuarios` | Lista de usuarios |
| `GET` | `/usuarios/{id}` | Obtiene un usuario por id |

## Configuración y base de datos

El archivo `src/main/resources/application.properties` apunta a una base de datos PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/backend_db
spring.datasource.username=backend_user
spring.datasource.password=backend_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

Ajusta estos valores según tu entorno de desarrollo.

## Requisitos para ejecutar la aplicación

1. JDK 25.
2. Maven, o el wrapper incluido (`mvnw`).
3. PostgreSQL en ejecución con la base de datos `backend_db` creada.
4. Variable `JAVA_HOME` apuntando al JDK.

## Cómo correr el proyecto

```bash
# Linux / macOS
./mvnw clean install
./mvnw spring-boot:run
```

```powershell
# Windows PowerShell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

Una vez iniciada, la documentación de la API queda disponible en <http://localhost:8080/swagger-ui.html>.

## Próximos pasos sugeridos

- Servicios y endpoints CRUD para el resto de entidades (comentarios, reacciones, perfiles, etc.)
- Mover la lógica de `UsuarioController` a una capa de servicio
- Manejo global de excepciones (`@RestControllerAdvice`) con excepciones propias y códigos HTTP adecuados
- Validación declarativa de DTOs (Bean Validation)
- Anotaciones OpenAPI (`@Tag`, `@Operation`) para enriquecer la documentación de Swagger
- Migraciones o scripts DDL para PostgreSQL
- Pruebas unitarias y de integración
