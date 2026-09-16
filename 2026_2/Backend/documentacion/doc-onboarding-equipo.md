# Documentación para onboarding del equipo

## 1. Objetivo

Esta guía está pensada para que cualquier miembro del equipo pueda preparar su entorno, entender el proyecto y comenzar a trabajar en el backend sin perder tiempo en configuración o procesos manuales.

El contexto actual del repositorio incluye varios ejemplos y una base de microservicio Java para la plataforma DNetwork.

## 2. Qué debe tener instalado

Antes de iniciar, cada desarrollador debe contar con lo siguiente:

### 2.1 Java JDK

Instalar un JDK compatible con el proyecto (idealmente 17+ o la versión indicada por el repositorio).

Verificar:

```bash
java -version
```

Luego definir la variable `JAVA_HOME`:

### Windows PowerShell

```powershell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
```

### Linux/macOS

```bash
export JAVA_HOME=/ruta/al/jdk
```

### 2.2 Maven

El proyecto usa Maven y además incluye wrapper (`mvnw`), pero es recomendable tener Maven instalado para facilitar comandos.

Verificar:

```bash
mvn -version
```

### 2.3 PostgreSQL

Necesitas tener PostgreSQL corriendo en local y la base de datos creada.

Ejemplo de configuración esperada:

- host: `localhost`
- puerto: `5432`
- base de datos: `backend_db`
- usuario: `backend_user`

## 3. Clonar y abrir el proyecto

```bash
git clone https://github.com/dpareja1394/ebackend.git
cd ebackend/2026_2/Backend
```

Luego dirígete al microservicio principal:

```bash
cd microservicios/dnetwork-java
```

## 4. Preparación del entorno local

### 4.1 Referencias de base de datos del proyecto

Antes de ejecutar la aplicación, es importante revisar los scripts SQL del proyecto, ubicados en:

- [`../database/create-db.sql`](../database/create-db.sql)
- [`../database/schema_red_social.sql`](../database/schema_red_social.sql)
- [`../database/data_test_red_social.sql`](../database/data_test_red_social.sql)

Estos archivos definen:

- creación del usuario PostgreSQL y la base de datos
- estructura del esquema relacional del sistema
- datos iniciales para pruebas y validación

### 4.2 Validar la versión de Java

```bash
java -version
```

### 4.2 Validar Maven

```bash
./mvnw -version
```

### 4.3 Verificar conexión a PostgreSQL

Asegúrate de que el servicio de PostgreSQL esté levantado. Si la base de datos no existe, créala con el nombre indicado en `application.properties`.

## 5. Ejecutar el proyecto

Desde la carpeta `microservicios/dnetwork-java`:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

En Windows PowerShell, si el JDK no está configurado:

```powershell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
./mvnw clean install
./mvnw spring-boot:run
```

## 6. Estructura del backend a tener en cuenta

El backend se organiza con separación por capas:

- `controller`: endpoints REST
- `service`: lógica de negocio
- `repository`: acceso a base de datos
- `domain`: entidades del negocio
- `dto`: transferencia de datos
- `mapper`: conversión entre entidades y DTOs
- `resources`: configuración de aplicación

### Ejemplo de paquetes vistos en el proyecto

```text
co.edu.usbcali.dnetwork_java
├── controller
├── domain
├── dto
├── mapper
├── repository
├── service
├── DnetworkJavaApplication.java
└── resources
```

## 7. Rutas y endpoints básicos

El proyecto ya incluye endpoints de prueba para validación inicial:

### Usuarios
- `GET /usuarios/ping`
- `GET /usuarios/validar-estado`
- `GET /usuarios/obtener-usuarios`
- `GET /usuarios/{id}`

### Etiquetas
- `GET /etiquetas/ping`
- `GET /etiquetas/validar-estado`
- `GET /etiquetas/obtener-etiquetas`
- `GET /etiquetas/{id}`

## 8. Convenciones de trabajo

### 8.1 Nombrado

- Clases en `PascalCase`
- Métodos en `camelCase`
- Packages en minúsculas
- Archivos Java con nombres descriptivos

### 8.2 Capas

- No mezclar lógica de negocio en el controlador
- No acceder directamente a la base de datos desde el controlador
- Los DTOs deben ser usados para comunicación fuera del dominio

### 8.3 Manejo de excepciones

Cada servicio debe validar entradas y lanzar errores con mensajes claros. Esto es crítico para evitar comportamiento oculto y errores difíciles de depurar.

## 9. Buenas prácticas para el equipo

- Mantener credenciales fuera del repositorio
- No subir archivos de configuración local con datos reales
- Usar ramas por funcionalidad o tarea
- Hacer commits con mensajes claros
- Documentar APIs nuevas y cambios de negocio
- Añadir pruebas cuando se implementa lógica nueva

## 10. Flujo recomendado de trabajo

1. Crear una rama desde `main` o desde la rama de trabajo vigente
2. Revisar tarea o historia de usuario
3. Implementar la funcionalidad en capas
4. Ejecutar validaciones locales
5. Hacer commit con mensaje claro
6. Abrir PR y solicitar revisión

## 11. Problemas comunes

### Error: JAVA_HOME no está definido

```text
The JAVA_HOME environment variable is not defined correctly
```

Solución:

```powershell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
```

### Error al conectar con PostgreSQL

Verifica:

- que PostgreSQL esté corriendo
- que la base de datos exista
- que el usuario y la contraseña coincidan con `application.properties`
- que el puerto 5432 esté disponible

### App compila pero no arranca

Revisa:

- si la base de datos está accesible
- si `application.properties` está bien configurado
- si el proyecto tiene dependencias pendientes

## 12. Sugerencias para avanzar con el proyecto

El equipo puede continuar con estos pasos:

- completar CRUD de entidades principales
- definir más responses y requests
- mejorar validaciones de negocio
- agregar manejo de excepciones globales
- implementar seguridad y autenticación
- preparar scripts SQL para inicializar la base de datos
- crear pruebas unitarias y de integración

## 13. Checklist rápido para empezar a trabajar

- [ ] Instalar JDK
- [ ] Configurar `JAVA_HOME`
- [ ] Instalar o verificar Maven
- [ ] Tener PostgreSQL activo
- [ ] Crear la base de datos de trabajo
- [ ] Clonar el repositorio
- [ ] Ejecutar `./mvnw clean install`
- [ ] Iniciar la app con `./mvnw spring-boot:run`
- [ ] Verificar endpoints básicos

---

Este documento sirve como guía de arranque para el equipo y como referencia para evitar errores de configuración y flujo de trabajo durante el desarrollo del backend.
