# Onboarding del equipo — Servicios

## 1. Propósito

Esta guía está diseñada para que cualquier integrante del equipo pueda entrar al proyecto, entender el negocio, configurar el entorno y comenzar a trabajar en el backend sin perder tiempo con definiciones ambiguas.

## 2. Contexto del proyecto

El proyecto está centrado en un sistema de gestión de compra-venta de vehículos para una concesionaria. El alcance incluye tanto procesos internos operativos como experiencia del comprador.

Se basa en:

- [Lista_Requerimientos_Funcionales.md](../Lista_Requerimientos_Funcionales.md)
- [result.md](../result.md)
- [database/schema.sql](../database/schema.sql)
- [microservices/autosusbcali](../microservices/autosusbcali/)

## 3. Requisitos mínimos del entorno

### 3.1 Java

Se requiere Java compatible con el proyecto.

Verificar:

```bash
java -version
```

Si es necesario, definir `JAVA_HOME`:

### Windows PowerShell

```powershell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
```

### Linux/macOS

```bash
export JAVA_HOME=/ruta/al/jdk
```

### 3.2 Maven

Verificar:

```bash
mvn -version
```

También puede usarse el wrapper incluido en el microservicio:

```bash
./mvnw -version
```

### 3.3 PostgreSQL

Es necesario contar con PostgreSQL disponible y con la base de datos creada.

## 4. Estructura del proyecto

```text
Servicios/
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
│       ├── src/
│       └── ...
├── documentacion/
│   ├── README.md
│   ├── doc-tecnica-servicios.md
│   └── doc-onboarding-equipo-servicios.md
└── ...
```

## 5. Cómo comenzar a trabajar

### Paso 1 — revisar la lógica del negocio

Lee primero:

- `Lista_Requerimientos_Funcionales.md`
- `result.md`
- `database/schema.sql`

Esto te da la visión del negocio y del modelo de datos que debe soportar la aplicación.

### Paso 2 — preparar la base de datos

Ejecuta los scripts del directorio `database` en este orden:

1. `create-db.sql`
2. `schema.sql`
3. `seed.sql`

Esto deja lista la estructura y algunos datos de prueba para la plataforma.

### Paso 3 — arrancar el microservicio

Desde la carpeta:

```bash
microservices/autosusbcali
```

Ejecuta:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## 6. Modelo funcional que debe manejarse

El sistema debe cubrir estas áreas:

- usuarios y roles
- inventario y vehículos
- cotizaciones y financiación
- reservas y ventas
- pagos y documentación legal
- postventa y garantías
- notificaciones
- auditoría y reportes

## 7. Convenciones recomendadas para trabajo en equipo

### 7.1 Arquitectura

Organiza la aplicación por capas:

- `controller`
- `service`
- `repository`
- `domain`
- `dto`
- `mapper`

### 7.2 Nomenclatura

- Clases: `PascalCase`
- Métodos: `camelCase`
- Paquetes: minúsculas
- Entidades JPA: acorde con la tabla SQL

### 7.3 Reglas de negocio

- no mezclar lógica de negocio en el controlador
- validar DTOs y entradas
- mantener consistencia de modelos
- no exponer entidades directamente en la API

## 8. Base de datos y negocio

El diseño del esquema incorpora reglas críticas como:

- validación de estados de vehículo
- tipos de usuario
- estados de crédito, ventas y pagos
- restricciones únicas por email, documento y número de factura
- integridad referencial en relaciones principales

Con esto se busca un sistema operativo y trazable, no solo una base de datos funcional.

## 9. Problemas comunes

### Error de Java / JAVA_HOME

```text
The JAVA_HOME environment variable is not defined correctly
```

Solución:

```powershell
$env:JAVA_HOME="C:\Ruta\Al\JDK"
```

### Error de conexión a PostgreSQL

Revisa:

- que PostgreSQL esté corriendo
- que la base de datos exista
- que el usuario y la contraseña coincidan
- que no haya puerto bloqueado

### App no inicia

Verifica:

- `application.properties`
- existencia de la base de datos
- dependencias Maven resueltas
- compilación de entidades JPA

## 10. Checklist de arranque

- [ ] Instalar Java y configurar `JAVA_HOME`
- [ ] Configurar Maven
- [ ] Tener PostgreSQL activo
- [ ] Crear la base de datos del proyecto
- [ ] Ejecutar scripts SQL
- [ ] Revisar requisitos funcionales
- [ ] Correr `./mvnw clean install`
- [ ] Iniciar `spring-boot:run`
- [ ] Validar endpoints base

## 11. Siguientes pasos recomendados

1. convertir el esquema SQL a entidades JPA
2. completar repositorios y servicios
3. construir las API REST por flujos principales
4. implementar seguridad y permisos
5. definir pruebas e integrar con PostgreSQL
6. preparar una versión final de la aplicación

---

Esta guía permite entrar al proyecto con claridad y trabajar en equipo con una base técnica y funcional consistente.
