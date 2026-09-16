# Documentación técnica del backend

## 1. Alcance y objetivo

Este repositorio contiene el material y la base de un backend académico/profesional orientado a servicios REST con Java y PostgreSQL. El proyecto más avanzado identificado hasta el momento es `Backend/microservicios/dnetwork-java`, que modela una red social con entidades y servicios para gestionar usuarios, perfiles, publicaciones, etiquetas y relaciones.

El objetivo del backend es ofrecer una API para gestionar la lógica de negocio de una plataforma social, con persistencia relacional, separación por capas y exposición de endpoints REST.

## 2. Stack tecnológico

- Java 25 (configurado en el proyecto `dnetwork-java`)
- Maven
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- JPA/Hibernate para el mapeo ORM

## 3. Estructura validada del proyecto

El microservicio `dnetwork-java` presenta la siguiente organización:

```text
Backend/
└── microservicios/
    └── dnetwork-java/
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
        │   │   │       ├── dto/
        │   │   │       │   ├── request/
        │   │   │       │   └── response/
        │   │   │       ├── mapper/
        │   │   │       ├── repository/
        │   │   │       ├── service/
        │   │   │       └── DnetworkJavaApplication.java
        │   │   └── resources/
        │   │       └── application.properties
        │   └── test/
        │       └── java/
        │           └── co/edu/usbcali/dnetwork_java/
        │               └── DnetworkJavaApplicationTests.java
        ├── pom.xml
        ├── mvnw
        ├── mvnw.cmd
        └── README.md
```

## 4. Capa de dominio

El dominio incluye entidades relacionadas con una plataforma social, entre ellas:

- `Usuario`
- `Perfil`
- `Publicacion`
- `Comentario`
- `Reaccion`
- `Etiqueta`
- `Relacion`
- `Bloqueo`
- `Denuncia`
- `Notificacion`
- `ActividadHistorial`
- `Medio`

Las entidades usan anotaciones JPA (`@Entity`, `@Table`, `@Column`, `@Id`, etc.) y se incluyen converters para enums que se persisten en base de datos.

## 5. Capa de acceso a datos

Se detectan repositorios de Spring Data JPA para cada entidad principal:

- `UsuarioRepository`
- `EtiquetaRepository`
- `PublicacionRepository`
- `ComentarioRepository`
- `RelacionRepository`
- `PublicacionEtiquetaRepository`
- `NotificacionRepository`
- `PerfilRepository`
- `BloqueoRepository`
- `DenunciaRepository`
- `ReaccionRepository`
- `MedioRepository`
- `ActividadHistorialRepository`

Esto permite ejecutar operaciones CRUD de forma estándar sin escribir SQL manual para las consultas básicas.

## 6. Capa de servicios y controladores

### Servicios

- `EtiquetaService`
- `EtiquetaServiceImpl`

Actualmente el servicio de etiquetas implementa lógica de consulta por ID y listado, con validaciones básicas de entrada.

### Controladores

- `EtiquetaController`
- `UsuarioController`

Endpoints detectados:

#### `/etiquetas`
- `GET /etiquetas/ping`
- `GET /etiquetas/validar-estado`
- `GET /etiquetas/obtener-etiquetas`
- `GET /etiquetas/{id}`

#### `/usuarios`
- `GET /usuarios/ping`
- `GET /usuarios/validar-estado`
- `GET /usuarios/obtener-usuarios`
- `GET /usuarios/{id}`

## 7. DTOs y mappers

Se observa una separación clara entre dominio y contratos de API:

- `dto/request` para entradas
- `dto/response` para salidas
- `mapper` para convertir entidades a DTOs y viceversa

Esto ayuda a mantener el modelo de dominio aislado de la capa HTTP.

## 8. Configuración de base de datos

La base de datos del proyecto está definida en PostgreSQL y se documenta en la carpeta `Backend/database`.

### Archivos relevantes

- [`../database/create-db.sql`](../database/create-db.sql) — crea el usuario PostgreSQL y la base de datos `backend_db`
- [`../database/schema_red_social.sql`](../database/schema_red_social.sql) — define el esquema completo del sistema
- [`../database/data_test_red_social.sql`](../database/data_test_red_social.sql) — carga datos de prueba para usuarios, publicaciones y relaciones
- [`../generacion_pojos.md`](../generacion_pojos.md) — guía para generar entidades JPA y repositorios a partir del esquema

El archivo de configuración principal del microservicio es:

```properties
src/main/resources/application.properties
```

Con una configuración tipo:

```properties
spring.application.name=dnetwork-java
spring.datasource.url=jdbc:postgresql://localhost:5432/backend_db
spring.datasource.username=backend_user
spring.datasource.driver-class-name=org.postgresql.Driver
```

La base de datos principal del proyecto usa PostgreSQL 17 y contempla los siguientes módulos:

- usuarios
- perfiles
- relaciones
- publicaciones
- medios
- etiquetas
- reacciones
- comentarios
- notificaciones
- bloqueos
- denuncias
- actividad_historial

Además, el esquema incluye tipos `ENUM`, claves foráneas, restricciones de unicidad y varios índices para búsquedas y joins frecuentes.

## 9. Estado real del proyecto

El proyecto se encuentra en una etapa inicial de desarrollo con una base sólida para continuar. Ya tiene:

- estructura Maven correcta
- dependencia de Spring Boot
- entidades del dominio
- repositorios JPA
- controladores de ejemplo
- DTOs y mappers
- configuración JDBC con PostgreSQL

Sin embargo, aún faltan tareas clave como:

- completar CRUD para todas las entidades
- manejo robusto de excepciones
- validaciones en DTOs
- seguridad y autenticación
- migraciones/versionado del esquema
- pruebas unitarias/integración
- documentación de endpoints y modelos

## 10. Validación realizada

Se validó la estructura y el alcance del proyecto en el repositorio. También se intentó ejecutar la prueba de Maven, pero el entorno actual carece de una configuración válida de Java, por lo que no se pudo compilar en este equipo:

```text
The JAVA_HOME environment variable is not defined correctly
```

Esto indica que, para ejecutar o validar el backend, primero debemos instalar/configurar JDK y definir `JAVA_HOME`.

## 11. Requisitos para ejecutar el proyecto

1. Java 17/21/25 compatible con el proyecto
2. Maven instalado o usar `mvnw`
3. PostgreSQL corriendo localmente
4. Base de datos creada y credenciales válidas
5. Variables de entorno o configuración local con credenciales no expuestas

## 12. Comandos de ejecución

```bash
# Windows PowerShell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
./mvnw clean install
./mvnw spring-boot:run
```

## 13. Recomendación de continuidad

El siguiente paso ideal es fortalecer la base que ya existe:

- completar servicios de negocio
- definir contratos REST claros
- manejar errores y validaciones
- definir modelos de datos finales
- preparar scripts SQL iniciales
- desglosar el backend en microservicios o módulos funcionales si el proyecto crece

---

Este documento sirve como base técnica del backend actual y como punto de partida para la evolución del proyecto.
