# Documentación del proyecto Servicios

Este directorio centraliza la documentación formal del proyecto de servicios backend para una concesionaria de vehículos.

## Índice

- [Documentación técnica](./doc-tecnica-servicios.md)
- [Onboarding del equipo](./doc-onboarding-equipo-servicios.md)

## Referencias de trabajo

- [Requisitos funcionales](../Lista_Requerimientos_Funcionales.md)
- [Borrador del modelo relacional](../result.md)
- [Generación de POJOs y repositorios](../generacion_pojos.md)
- [Scripts SQL](../database/)
- [Microservicio Java](../microservices/autosusbcali/)

## Resumen ejecutivo

El proyecto está basado en un caso de negocio realista de una concesionaria de autos, con muchos flujos transaccionales: usuarios, inventario, reservas, cotizaciones, financiamiento, pagos, ventas, garantías, documentación legal, notificaciones y auditoría.

La solución está planteada como un backend en Java/Spring Boot con base de datos PostgreSQL, usando una estructura de dominio, repositorios, servicios y endpoints REST.

## Modelado de datos

El esquema principal quedó definido en:

- `../database/schema.sql`

Este archivo identifica entidades clave y módulos como:

- usuarios y accesos
- inventario vehicular
- cotizaciones y financiamiento
- ventas y reservas
- pagos y facturación
- postventa
- notificaciones
- reportes y administración

## Siguiente evolución sugerida

1. Definir entidades JPA finales en el microservicio
2. Crear repositorios y servicios por módulo
3. Implementar controladores REST para cada flujo
4. Gestionar validaciones de negocio con DTOs
5. Añadir pruebas unitarias e integración
6. Preparar seguridad y autenticación del sistema

---

Documentación de referencia para el proyecto de servicios del curso.
