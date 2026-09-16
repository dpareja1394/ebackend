# Documentación técnica del proyecto Servicios

## 1. Alcance y objetivo

Este proyecto propone un backend para una concesionaria de vehículos con un enfoque académico y aplicable a un entorno real. El caso de uso principal gira en torno a la gestión integral del negocio: registro de usuarios, inventario, búsquedas, cotizaciones, crédito, ventas, pagos y postventa.

La línea base del diseño se soporta en:

- [Lista_Requerimientos_Funcionales.md](../Lista_Requerimientos_Funcionales.md)
- [result.md](../result.md)
- [database/schema.sql](../database/schema.sql)
- [microservices/autosusbcali](../microservices/autosusbcali/)

## 2. Stack tecnológico

- Java 25 (configurado en el microservicio base)
- Maven
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok

## 3. Estructura del repositorio

```text
2026_2/
└── Servicios/
    ├── README.md
    ├── Lista_Requerimientos_Funcionales.md
    ├── result.md
    ├── generacion_pojos.md
    ├── database/
    │   ├── create-db.sql
    │   ├── schema.sql
    │   └── seed.sql
    ├── microservices/
    │   └── autosusbcali/
    │       ├── pom.xml
    │       ├── mvnw
    │       ├── src/
    │       └── ...
    └── documentacion/
        ├── README.md
        ├── doc-tecnica-servicios.md
        └── doc-onboarding-equipo-servicios.md
```

## 4. Requisitos funcionales identificados

La base del negocio está definida en la lista de requisitos funcionales y comprende varios módulos clave:

### GU — Gestión de usuarios y accesos
- registro, autenticación y roles
- recuperación de contraseña
- gestión de permisos por módulo

### GV — Gestión de inventario de vehículos
- registro de vehículos
- publicación en catálogo
- estados del ciclo de vida
- carga de imágenes y ficha técnica

### BF — Búsqueda y filtros
- búsqueda por criterios
- comparador de vehículos
- alertas y búsquedas guardadas

### CF — Cotización y financiamiento
- cotizaciones con impuestos y descuentos
- simulador de crédito
- envío de solicitudes financieras
- aprobación o rechazo del crédito

### PV — Proceso de venta
- reservas
- asignación de asesores
- negociaciones
- cierre de venta

### DL — Documentación legal
- documentos del vehículo
- verificación RUNT
- contratos y traspasos

### PG — Pagos
- pagos iniciales, cuota inicial y saldo
- pasarela de pagos
- facturación DIAN

### PS — Postventa
- garantías
- citas de servicio
- PQRS

### NT — Notificaciones
- avisos automáticos del proceso
- alertas por coincidencias guardadas

### RA — Reportes y administración
- KPI de ventas
- inventario y auditoría
- parámetros del sistema

## 5. Modelado de base de datos

El esquema SQL define la estructura relacional principal en PostgreSQL. Se observa una base sólida con normalización y separación por módulos funcionales.

### 5.1 Entidades principales

- `roles`
- `permisos`
- `usuarios`
- `tokens_recuperacion`
- `marcas`
- `modelos`
- `vehiculos`
- `fichas_tecnicas`
- `imagenes_vehiculo`
- `documentos_vehiculo`
- `verificaciones_runt`
- `busquedas_guardadas`
- `cotizaciones`
- `accesorios_cotizacion`
- `entidades_financieras`
- `solicitudes_credito`
- `oportunidades_venta`
- `reservas`
- `negociaciones`
- `ventas`
- `contratos`
- `tramites_traspaso`
- `pagos`
- `transacciones_pasarela`
- `facturas`
- `garantias`
- `citas_servicio`
- `pqrs`
- `notificaciones`
- `auditoria`
- `parametros_sistema`

### 5.2 Reglas de negocio incorporadas en SQL

El esquema incluye diversas restricciones importantes:

- CHECK en tipos de usuarios (`INTERNO`, `COMPRADOR`)
- CHECK en estado de vehículos
- CHECK en estado de cotizaciones y ventas
- CHECK en permisos por módulo y acción
- UNIQUE en email, documento y claves de negocio
- PK compuestas en tablas de relación
- validaciones de numerics y rangos

### 5.3 Indicadores de base de datos

Los índices creados incluyen:

- usuarios por email y documento
- tokens de recuperación
- vehículos por modelo, estado y precio
- reservas, oportunidades y cotizaciones
- búsquedas guardadas con `GIN` sobre JSONB
- auditoría y reportes

Esto evidencia una intención clara de diseño para consultas frecuentes y trazabilidad operativa.

## 6. Configuración de base de datos

La base de datos se prepara a partir de los scripts:

- [create-db.sql](../database/create-db.sql)
- [schema.sql](../database/schema.sql)
- [seed.sql](../database/seed.sql)

### Ejemplo base y usuario

```sql
CREATE USER backend_user WITH ENCRYPTED PASSWORD 'backend_password';
CREATE DATABASE backend_db OWNER backend_user;
```

La base de datos del proyecto está pensada para entorno local y desarrollo académico, con un flujo de creación del esquema y carga de datos de ejemplo.

## 7. Microservicio Java

El proyecto cuenta con un microservicio Spring Boot bajo:

- [microservices/autosusbcali](../microservices/autosusbcali) 

Este proyecto ya presenta:

- configuración base de Maven
- Spring Boot 4.1.1
- JPA y Web MVC
- Postgres driver
- Lombok

La intención es que el modelo de datos de PostgreSQL se refleje luego en entidades JPA y repositorios Spring Data JPA.

## 8. Estado actual

Actualmente el proyecto se encuentra en una etapa de:

- diagnóstico funcional
- diseño lógico del modelo de datos
- configuración inicial de backend
- preparación del esquema SQL base

Se recomienda avanzar en las siguientes prioridades:

1. finalización de entidades JPA
2. implementación de repositorios por módulo
3. servicios de negocio con reglas transaccionales
4. controladores REST para cada flujo
5. validación de DTOs y manejo de errores
6. autenticación, autorización y seguridad
7. pruebas unitarias e integración

## 9. Hitos recomendados

### Hito 1 — Base y autenticación
- usuarios
- roles y permisos
- login y recuperación

### Hito 2 — Inventario y catálogo
- marcas, modelos, vehículos
- imágenes y documentos
- publicación en catálogo

### Hito 3 — Venta y financiación
- cotizaciones
- solicitudes de crédito
- reservas y negociaciones

### Hito 4 — Cobranza y postventa
- pagos
- facturas
- garantías, citas y PQRS

### Hito 5 — Operación y observabilidad
- auditoría
- parámetros de sistema
- notificaciones
- reportes

## 10. Recomendación de continuidad

La base de datos y los requerimientos ya dan una línea clara para la implementación. El siguiente paso más natural es convertir el esquema SQL en entidades JPA usando la guía indicada en `generacion_pojos.md` y luego completar los repositorios, servicios y controladores del microservicio.

---

El proyecto cuenta con una base técnica sólida para continuar con un backend serio y funcional de una concesionaria.
