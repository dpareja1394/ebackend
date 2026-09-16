# Dnetwork Java

Microservicio backend desarrollado en Java con Spring Boot para la plataforma DNetwork. El proyecto está estructurado como una API REST con acceso a base de datos PostgreSQL y mapeo ORM con JPA/Hibernate.

## Estado del proyecto

Actualmente el servicio presenta una base funcional de estructura y dominio:

- Configuración inicial de Spring Boot 4.1.0
- Dependencias para Web MVC y JPA
- Soporte para PostgreSQL
- Entidades JPA para el dominio principal de la red social
- Repositorios de Spring Data
- Controladores REST básicos para usuarios y etiquetas
- Mapper y DTOs para respuesta de datos
- Pruebas iniciales de arranque de la aplicación

Se observa que el proyecto está en etapa de base arquitectónica / desarrollo inicial, con varias entidades del dominio ya modeladas y endpoints básicos creados.

## Stack tecnológico

- Java 25
- Maven
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok

## Estructura validada del proyecto

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
│   │   │       │   └── converter/
│   │   │       │       ├── EstadoDenunciaConverter.java
│   │   │       │       ├── EstadoRelacionConverter.java
│   │   │       │       ├── PrivacidadPublicacionConverter.java
│   │   │       │       ├── TipoMedioConverter.java
│   │   │       │       ├── TipoNotificacionConverter.java
│   │   │       │       ├── TipoReaccionConverter.java
│   │   │       │       ├── ValorDbEnumConverter.java
│   │   │       │       └── VisibilidadPerfilConverter.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   └── CrearEtiquetaRequest.java
│   │   │       │   └── response/
│   │   │       │       ├── ObtenerEtiquetaResponse.java
│   │   │       │       └── ObtenerUsuarioResponse.java
│   │   │       ├── mapper/
│   │   │       │   ├── EtiquetaMapper.java
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
│   │   │       │   └── EtiquetaServiceImpl.java
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

## Dominio identificado

El proyecto modela conceptos propios de una red social o plataforma de contenidos, incluyendo:

- Usuarios
- Perfiles
- Etiquetas
- Publicaciones
- Comentarios
- Reacciones
- Relaciones entre usuarios
- Bloqueos
- Denuncias
- Notificaciones
- Actividad de historial
- Medios asociados a publicaciones

Esto indica que la aplicación está orientada a un backend de una red social con lógica de interacción y contenido generado por usuarios.

## Endpoints iniciales

Se detectan controladores con endpoints básicos:

### `/etiquetas`

- `GET /etiquetas/ping` → respuesta de prueba
- `GET /etiquetas/validar-estado` → validación de estado del servicio
- `GET /etiquetas/obtener-etiquetas` → lista de etiquetas
- `GET /etiquetas/{id}` → obtiene una etiqueta por id

### `/usuarios`

- `GET /usuarios/ping` → respuesta de prueba
- `GET /usuarios/validar-estado` → validación de estado del servicio
- `GET /usuarios/obtener-usuarios` → lista de usuarios
- `GET /usuarios/{id}` → obtiene un usuario por id

## Configuración y base de datos

El archivo `src/main/resources/application.properties` apunta a una base de datos PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/backend_db
spring.datasource.username=backend_user
spring.datasource.driver-class-name=org.postgresql.Driver
```

Se asume que la conexión y credenciales se configuran localmente o mediante variables de entorno según el entorno de desarrollo.

## Validación realizada

Se validó la estructura del proyecto y el árbol de archivos del microservicio. La organización sigue la convención estándar de un proyecto Spring Boot con separación por capas:

- `controller` → endpoints REST
- `service` → lógica de negocio
- `repository` → acceso a datos con JPA
- `domain` → entidades del negocio
- `dto` → contratos de entrada/salida
- `mapper` → conversión entre entidades y DTOs
- `resources` → configuración de la app

## Requisitos para ejecutar la aplicación

1. Tener instalado JDK 25 o compatible con el proyecto.
2. Tener Maven disponible o usar el wrapper incluido (`mvnw`).
3. Tener PostgreSQL corriendo con la base de datos configurada.
4. Definir correctamente `JAVA_HOME` antes de ejecutar Maven.

## Cómo correr el proyecto

```bash
# En Windows PowerShell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
./mvnw clean install
./mvnw spring-boot:run
```

## Observación importante

La validación de compilación actual no pudo completarse en este entorno porque no hay un JDK configurado. Al ejecutar Maven se reportó:

```text
The JAVA_HOME environment variable is not defined correctly
```

Esto indica que antes de compilar o levantar el servicio, es necesario instalar/configurar Java y establecer la variable `JAVA_HOME` correctamente.

## Próximo paso sugerido

Completar la configuración del entorno Java y continuar con:

- implementación de servicios completos para entidades principales
- creación de controladores REST con CRUD robusto
- validación de DTOs, excepciones y manejo de errores
- configuración de migraciones o scripts DDL para PostgreSQL
- pruebas unitarias/integración
