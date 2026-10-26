<!--
Sync Impact Report
- Version change: 1.0.0 → 1.0.1 (PATCH)
  - III. Contenedores y Configuración Externa: aclarado que el Dockerfile propio aplica a
    componentes propios; la infraestructura (bases de datos) usa imagen oficial con versión fija.
    Origen: /speckit-analyze hallazgo D3 sobre 001-gestion-parqueaderos.
- Historial: (plantilla sin ratificar) → 1.0.0
- Principios definidos (nuevos):
  - [PRINCIPLE_1_NAME] → I. Microservicios Autónomos
  - [PRINCIPLE_2_NAME] → II. Contratos de API Primero y Gateway Único
  - [PRINCIPLE_3_NAME] → III. Contenedores y Configuración Externa
  - [PRINCIPLE_4_NAME] → IV. Observabilidad y Resiliencia
  - [PRINCIPLE_5_NAME] → V. Simplicidad y Calidad Pragmática
- Secciones añadidas: Stack Tecnológico y Restricciones; Flujo de Desarrollo; Governance
- Secciones eliminadas: ninguna
- Plantillas: no modificadas (las leen en tiempo de ejecución)
  - .specify/templates/plan-template.md ✅ compatible (Constitution Check usa estos principios)
  - .specify/templates/spec-template.md ✅ compatible
  - .specify/templates/tasks-template.md ✅ compatible (pruebas opcionales, alineado con Principio V)
- TODOs diferidos: ninguno
-->

# Parqueaderos Constitution

## Core Principles

### I. Microservicios Autónomos

- Cada microservicio MUST tener una única responsabilidad de negocio (p. ej. parqueaderos,
  espacios, reservas, pagos, usuarios) y desplegarse de forma independiente.
- Cada microservicio MUST ser dueño de sus datos: ningún servicio accede directamente a la
  base de datos de otro; la comunicación entre servicios ocurre solo por su API pública.
- Un servicio MUST poder implementarse en Spring Boot o en Go; la elección se justifica en el
  plan de la funcionalidad (p. ej. Go para servicios de alto rendimiento o baja huella).
- Se MUST NOT crear servicios sin responsabilidad de negocio clara (solo "organizacionales").

**Razón**: la autonomía permite escalar, desplegar y cambiar tecnología por servicio, que es
el objetivo central de una arquitectura en la nube.

### II. Contratos de API Primero y Gateway Único

- Todo microservicio MUST exponer una API REST/JSON documentada con OpenAPI antes o junto con
  su implementación; el contrato es la fuente de verdad entre equipos y lenguajes.
- Las rutas MUST estar versionadas (`/api/v1/...`); un cambio incompatible exige nueva versión.
- El frontend Angular y los clientes externos MUST consumir los servicios exclusivamente a
  través del API Gateway, que enruta y distribuye las peticiones entre los servicios Spring
  Boot y Go.
- Los errores MUST usar un formato JSON uniforme (código, mensaje, detalle) y códigos HTTP
  correctos en todos los servicios, independientemente del lenguaje.

**Razón**: con servicios heterogéneos (Java y Go), solo contratos explícitos y un punto de
entrada único mantienen la integración predecible.

### III. Contenedores y Configuración Externa

- Cada componente propio (microservicios, API Gateway, frontend) MUST tener un `Dockerfile`
  reproducible (multi-stage cuando aplique) y ejecutarse como contenedor.
- Los servicios de infraestructura (p. ej. bases de datos) MUST ejecutarse como contenedor a
  partir de una imagen oficial con versión fija (nunca `latest`); un `Dockerfile` propio solo
  se crea si la imagen necesita personalización.
- El sistema completo MUST poder levantarse localmente con un solo comando
  (`docker compose up`).
- La configuración y los secretos MUST provenir de variables de entorno; se MUST NOT
  versionar credenciales ni valores específicos de un entorno en el código.
- Los contenedores MUST ser sin estado; el estado persistente vive en servicios de datos.

**Razón**: los contenedores garantizan paridad entre desarrollo y nube y permiten desplegar
en cualquier proveedor sin cambios de código.

### IV. Observabilidad y Resiliencia

- Cada servicio MUST exponer un endpoint de salud (Spring Actuator `/actuator/health` o
  `/health` en Go) usado por Docker y por el gateway.
- Los logs MUST ser estructurados (JSON) y escribirse en stdout/stderr.
- Cada petición MUST propagar un identificador de correlación (`X-Correlation-Id`) desde el
  gateway a todos los servicios involucrados.
- Las llamadas entre servicios MUST definir timeouts y manejar fallos del servicio remoto sin
  propagar errores no controlados.

**Razón**: en un sistema distribuido, sin trazabilidad y tolerancia a fallos los errores son
imposibles de diagnosticar.

### V. Simplicidad y Calidad Pragmática

- Se MUST empezar por la solución más simple que cumpla la especificación (YAGNI); cualquier
  complejidad adicional (nuevo servicio, mensajería, caché) MUST justificarse en el plan.
- Las pruebas son RECOMENDADAS, no bloqueantes: se SHOULD escribir pruebas unitarias para la
  lógica de negocio y pruebas de integración para los endpoints críticos (p. ej. registro de
  entrada/salida de vehículos y cobro).
- Las reglas de negocio SHOULD validarse antes de ejecutar transacciones o persistir datos.

**Razón**: el proyecto prioriza entregas funcionales y aprendizaje de arquitectura; la calidad
se fomenta sin frenar la iteración.

## Stack Tecnológico y Restricciones

- **Microservicios Java**: Spring Boot 3.x, Java 21 (LTS), Maven, Spring Data JPA.
- **Microservicios Go**: Go 1.22+ con biblioteca estándar o framework ligero (p. ej. Gin/Echo).
- **API Gateway**: Spring Cloud Gateway u otra alternativa contenerizada; la elección se
  documenta en el plan de la primera funcionalidad que lo requiera.
- **Frontend**: Angular (versión LTS vigente) con TypeScript, servido en su propio contenedor.
- **Persistencia**: base de datos relacional (p. ej. PostgreSQL) por servicio; otra tecnología
  de datos MUST justificarse en el plan.
- **Despliegue**: Docker y Docker Compose como base; la orquestación en nube se define por
  funcionalidad sin romper los Principios I–IV.
- La estructura del repositorio MUST separar cada componente en su propio directorio
  (p. ej. `services/<nombre>`, `gateway/`, `frontend/`).

## Flujo de Desarrollo

- Cada funcionalidad sigue el flujo Spec Kit: especificar → (aclarar) → planificar → tareas →
  implementar.
- El plan de cada funcionalidad MUST incluir el "Constitution Check" y justificar cualquier
  desviación en la tabla de complejidad.
- Los commits SHOULD ser pequeños y descriptivos; el trabajo se integra a `main` mediante
  ramas por funcionalidad.
- Antes de integrar, el sistema MUST construir y levantar con `docker compose` sin errores, y
  las pruebas existentes MUST pasar.

## Governance

- Esta constitución prevalece sobre cualquier otra práctica del proyecto; ante conflicto,
  se corrige la práctica o se enmienda la constitución.
- Las enmiendas MUST documentarse en este archivo con su Sync Impact Report, incrementar la
  versión y actualizar la fecha de última enmienda.
- Versionado semántico: MAJOR para eliminar o redefinir principios de forma incompatible;
  MINOR para agregar principios o secciones o ampliar materialmente la guía; PATCH para
  aclaraciones y correcciones de redacción.
- Cada plan, revisión y PR MUST verificar el cumplimiento de los principios; toda complejidad
  añadida MUST quedar justificada.

**Version**: 1.0.1 | **Ratified**: 2026-10-26 | **Last Amended**: 2026-10-26
