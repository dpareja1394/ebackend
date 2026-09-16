# Documentación del backend

Este directorio centraliza la documentación formal del backend del proyecto y referencia los artefactos principales relacionados con la arquitectura, la base de datos y el microservicio Java.

## Índice

- [Documentación técnica del backend](./doc-tecnica-backend.md)
- [Onboarding del equipo](./doc-onboarding-equipo.md)

## Referencias cruzadas

- [Base de datos del proyecto](../database/)
- [Script de creación de la base de datos](../database/create-db.sql)
- [Esquema principal de la red social](../database/schema_red_social.sql)
- [Datos de prueba](../database/data_test_red_social.sql)
- [Generación de POJOs / entidades](../generacion_pojos.md)
- [Microservicio Java](../microservicios/dnetwork-java/README.md)

## Resumen del sistema

El backend se enfoca en una aplicación de red social con persistencia relacional en PostgreSQL. El artefacto principal es el microservicio `dnetwork-java`, que incluye:

- entidades de dominio JPA
- repositorios Spring Data JPA
- controladores REST
- DTOs y mappers
- configuración de base de datos
- scripts SQL de esquema y datos de prueba

## Base de datos

El modelo de datos está definido en PostgreSQL y se encuentra en:

- `../database/schema_red_social.sql` — esquema principal con tablas, tipos ENUM, PK, FK e índices.
- `../database/create-db.sql` — creación del usuario y la base de datos.
- `../database/data_test_red_social.sql` — datos de prueba para usuarios, relaciones, publicaciones, etiquetas y notificaciones.

La base de datos del proyecto cubre entidades como:

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

## Uso recomendado

1. Revisar la documentación técnica para entender la arquitectura.
2. Revisar la guía de onboarding para preparar el entorno local.
3. Consultar los scripts SQL para la estructura y carga inicial de datos.
4. Revisar el microservicio Java para contenido técnico específico del backend.

---

Documento de referencia para el repositorio del curso y para uso en GitHub.
