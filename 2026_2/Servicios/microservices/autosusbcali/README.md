# Autosusbcali

Microservicio backend desarrollado en Java con Spring Boot para la plataforma Autosusbcali. Expone una API REST y utiliza PostgreSQL como base de datos, con persistencia mediante Spring Data JPA e Hibernate.

## Estado del proyecto

El proyecto cuenta con una estructura inicial funcional y un primer conjunto de operaciones para la gestión de roles:

- Aplicación Spring Boot con configuración para PostgreSQL.
- Entidades JPA para los dominios de usuarios, vehículos, ventas, pagos, reservas y servicios, entre otros.
- Repositorios Spring Data para las entidades del dominio.
- API REST para consultar y crear roles.
- DTOs y mapper para separar las entidades de los contratos de la API.
- Prueba inicial para verificar el arranque del contexto de Spring.

La implementación está en desarrollo. En particular, el endpoint para modificar roles todavía no tiene lógica de servicio implementada.

## Stack tecnológico

- Java 25
- Maven, con Maven Wrapper incluido
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- JUnit y Spring Boot Test

## Estructura del proyecto

```text
autosusbcali/
├── src/
│   ├── main/
│   │   ├── java/co/edu/usbcali/autosusbcali/
│   │   │   ├── controller/
│   │   │   ├── domain/
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── AutosusbcaliApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/co/edu/usbcali/autosusbcali/
│           └── AutosusbcaliApplicationTests.java
├── .mvn/wrapper/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Arquitectura por capas

- `controller`: recepción y respuesta de solicitudes HTTP.
- `service`: lógica de negocio, con interfaz (`IRolService`) e implementación (`RolService`).
- `repository`: acceso a datos mediante JPA.
- `domain`: entidades persistidas.
- `dto/request` y `dto/response`: contratos de entrada y salida; los DTOs de rol están implementados como `record`.
- `mapper`: conversión entre entidades y DTOs.

## Dominio identificado

El modelo incluye entidades relacionadas con la operación de una plataforma automotriz, entre ellas:

- Usuarios, roles y permisos.
- Vehículos, marcas, modelos, imágenes y fichas técnicas.
- Oportunidades de venta, ventas, cotizaciones y negociaciones.
- Reservas, citas de servicio, garantías y documentos del vehículo.
- Entidades financieras, solicitudes de crédito, pagos y facturas.
- Trámites de traspaso, verificación RUNT y transacciones de pasarela.
- Notificaciones, auditoría, PQRS y parámetros del sistema.

## API disponible

Los endpoints de roles tienen como prefijo `/api/roles`.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/roles` | Lista todos los roles. |
| `GET` | `/api/roles/activos` | Lista los roles activos. |
| `GET` | `/api/roles/inactivos` | Lista los roles inactivos. |
| `GET` | `/api/roles/por-nombre/{nombre}` | Busca un rol por nombre. |
| `GET` | `/api/roles/por-id/{id}` | Busca un rol por identificador. |
| `POST` | `/api/roles/guardar` | Crea un rol y responde `201 Created`. |
| `PUT` | `/api/roles/modificar/{id}` | Ruta prevista para modificar un rol; la lógica del servicio está pendiente. |

### Crear un rol

`POST /api/roles/guardar`

```json
{
  "nombre": "Administrador",
  "descripcion": "Rol con permisos de administración"
}
```

El nombre y la descripción son obligatorios y no pueden estar vacíos. El nombre debe ser único. Al crear un rol, este queda activo inicialmente.

Ejemplo de respuesta:

```json
{
  "id": 1,
  "nombre": "Administrador"
}
```

Las respuestas de roles exponen los campos `id` y `nombre`; la descripción y el estado no hacen parte del DTO de respuesta.

### Errores y documentación interactiva

La aplicación todavía no define un manejo global de excepciones. Por ello, aunque las búsquedas por id o nombre contemplan un `404` en el controlador, el servicio actualmente lanza una excepción cuando no encuentra el rol y esa respuesta no está normalizada. El comportamiento HTTP de esos casos debe considerarse pendiente.

Swagger/OpenAPI no está configurado actualmente en este microservicio; el `pom.xml` no incluye una dependencia de `springdoc-openapi`.

## Configuración de la base de datos

La configuración de desarrollo en `src/main/resources/application.properties` apunta a PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/servicios_db
spring.datasource.username=servicios_user
spring.datasource.driver-class-name=org.postgresql.Driver
```

Antes de iniciar la aplicación, crea o habilita la base de datos `servicios_db` y configura las credenciales de conexión para tu entorno. No guardes contraseñas reales en el repositorio.

## Requisitos

1. JDK 25 instalado y disponible mediante `JAVA_HOME`.
2. PostgreSQL en ejecución y accesible con la configuración del proyecto.
3. Maven instalado o uso del wrapper incluido.

## Ejecutar y probar

Desde la carpeta del microservicio:

```bash
# Linux / macOS
./mvnw clean test
./mvnw spring-boot:run
```

```powershell
# Windows PowerShell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

La aplicación requiere que PostgreSQL esté disponible al arrancar. La prueba existente (`contextLoads`) valida que el contexto de Spring pueda iniciar con la configuración del entorno.

## Pendientes observados

- Implementar la lógica de modificación de roles (`PUT /api/roles/modificar/{id}`).
- Completar pruebas unitarias y de integración para los servicios y endpoints.
- Definir manejo uniforme de errores y respuestas para recursos no encontrados.
