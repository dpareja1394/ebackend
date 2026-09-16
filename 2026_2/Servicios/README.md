# Servicios

Este directorio concentra la documentación, la base de datos y la estructura preliminar del proyecto académico de servicios backend para la gestión de compra-venta de vehículos de una concesionaria.

## Objetivo del proyecto

El sistema tiene como propósito apoyar el flujo completo de una concesionaria: desde el registro de usuarios y autenticación, hasta el inventario, cotización, financiamiento, ventas, pagos, postventa, notificaciones y administración.

## Artefactos principales

- [Lista de requisitos funcionales](./Lista_Requerimientos_Funcionales.md)
- [Borrador de entidades y modelado relacional](./result.md)
- [Generación de POJOs y repositorios](./generacion_pojos.md)
- [Base de datos](./database/)
- [Microservicio Java](./microservices/autosusbcali/)

## Documentación formal

- [Índice de documentación](./documentacion/README.md)
- [Documentación técnica del backend](./documentacion/doc-tecnica-servicios.md)
- [Guía de onboarding del equipo](./documentacion/doc-onboarding-equipo-servicios.md)

## Arquitectura del sistema

El proyecto está orientado a un backend con arquitectura hexagonal / por capas, con intención de implementar un microservicio en Java Spring Boot y un modelo relacional en PostgreSQL.

### Componentes clave

- `database/` — scripts SQL de esquema, usuario DB y datos de prueba
- `microservices/autosusbcali/` — microservicio base para la aplicación
- `result.md` — propuesta inicial de entidades, tablas y campos
- `Lista_Requerimientos_Funcionales.md` — requisitos de negocio que sostienen el diseño de datos

## Base de datos

El esquema principal se encuentra en:

- [database/create-db.sql](./database/create-db.sql)
- [database/schema.sql](./database/schema.sql)
- [database/seed.sql](./database/seed.sql)

El modelo base cubre módulos como:

- usuarios y roles
- inventario de vehículos
- búsqueda y filtros
- cotización y financiación
- ventas y reservas
- documentación legal
- pagos y facturación
- postventa
- notificaciones
- auditoría y parámetros de sistema

## Estado actual del proyecto

El proyecto se encuentra en una etapa de diseño técnico inicial, con:

- requisitos funcionales claros
- borrador de modelado relacional
- scripts SQL preliminares
- microservicio Java en construcción

Falta aún consolidar:

- implementación final de entidades JPA
- repositorios y servicios
- validaciones y reglas de negocio
- pruebas automatizadas
- endpoints REST finales
- seguridad y autenticación

## Requisitos para continuar

1. Java JDK compatible con el proyecto
2. Maven o wrapper Maven (`mvnw`)
3. PostgreSQL 15+ o 17
4. base de datos creada y accesible
5. configuración de `application.properties` y credenciales locales

---

Este repositorio está preparado para servir como base de trabajo académico y de desarrollo del backend de concesionaria.
