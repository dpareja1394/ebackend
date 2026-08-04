# Electiva: Desarrollo Backend (USB Cali) - 2026-2

Repositorio de apoyo y materiales para la asignatura "Electiva de Desarrollo Backend" dirigida a estudiantes de Ingeniería Multimedia en la Universidad San Buenaventura Cali.

Este directorio contiene ejemplos, guías y ejercicios relacionados con:

- Desarrollo en Java (aplicaciones backend, APIs REST)
- Bases de datos relacionales usando PostgreSQL
- Desarrollo en Go (servicios y utilidades backend)

Carpeta del curso: [Backend](/home/daniel-pareja-londono/Proyectos/USBCali/ebackend/2026_2/Backend)

---

## Tecnologías principales

- Java 11+ (se recomienda Java 17+ LTS) — frameworks sugeridos: Spring Boot / Jakarta EE
- Go 1.20+ — para microservicios livianos y utilidades
- PostgreSQL 12+ — sistema gestor de base de datos relacional
- Herramientas: Maven o Gradle (Java), go modules (Go), psql/pgcli para DB

---

## Configuración local (resumen)

A continuación se indican los pasos básicos para preparar el entorno de trabajo en una máquina local.

1) Instalar Java

- Instalar JDK 11/17 según preferencia. Verificar:
  java -version

2) Instalar Go

- Instalar Go (1.20+). Verificar:
  go version

3) Instalar PostgreSQL

- Instalar PostgreSQL y crear una base de datos para ejercicios. Ejemplo básico con psql:

  - Iniciar sesión en psql como superusuario:
    sudo -u postgres psql

  - Crear usuario y base de datos (ajustar contraseña):
    CREATE USER usb_cali WITH PASSWORD 'cambiar_esta_contraseña';
    CREATE DATABASE electiva_backend OWNER usb_cali;

  - Salir:
    \q

4) Variables de entorno y configuración

- Para proyectos Java/Go, configurar las variables de conexión a la DB (host, puerto, nombre, usuario, contraseña).
- Evitar subir credenciales al repositorio. Usar archivos de configuración locales o variables de entorno (.env, application.properties, etc.).

Ejemplo de URI de conexión PostgreSQL:

  postgres://usb_cali:cambiar_esta_contraseña@localhost:5432/electiva_backend

---

## Scripts SQL de ejemplo

- Colocar scripts de creación y datos de ejemplo en `sql/`.
- Un script típico incluye la creación de esquemas, tablas y algunos inserts para pruebas.

---

## Ejecución rápida de ejemplos

Java (Spring Boot - ejemplo):

- Desde la carpeta `java/mi-proyecto` ejecutar:

  mvn clean package
  mvn spring-boot:run

- O ejecutar el jar:
  java -jar target/mi-proyecto-0.0.1-SNAPSHOT.jar

Go (servicio simple):

- Desde la carpeta `go/mi-servicio`:

  go build -o mi-servicio
  ./mi-servicio

- O ejecutar directamente durante el desarrollo:
  go run ./cmd/mi-servicio

---

## Buenas prácticas y recomendaciones

- Mantener las credenciales fuera del control de versiones (usar .gitignore y variables de entorno).
- Documentar cada ejercicio con un README dentro de su carpeta (por ejemplo `java/mi-proyecto/README.md`).
- Usar migraciones para la base de datos (Flyway o Liquibase en Java, o migr herramientas en Go) para mantener el esquema reproducible.
- Proveer instrucciones claras para evaluación: cómo ejecutar, endpoints disponibles y datos de prueba.

---

## Evaluación y entregables (sugerido)

- Ejercicios prácticos individuales y en grupo: APIs CRUD en Java, integración con PostgreSQL.
- Mini-proyecto final: servicio en Go que consuma o exponga datos y persista en PostgreSQL.
- Entregables: repositorio con código, scripts SQL, README del proyecto y un breve documento (PDF) con instrucciones de ejecución.

---

## Recursos y referencias

- PostgreSQL: https://www.postgresql.org/
- Spring Boot: https://spring.io/projects/spring-boot
- Go: https://golang.org/
- Flyway: https://flywaydb.org/

---

## Contacto

Profesor Daniel Pareja Londoño
+57 3022223712
---

