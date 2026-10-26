# Feature Specification: Gestión de Parqueaderos, Espacios y Vehículos

**Feature Branch**: `001-gestion-parqueaderos`

**Created**: 2026-10-26

**Status**: Draft

**Input**: User description: "gestión de parqueaderos, espacios y vehículos que pertenecen al parqueadero"

## Clarifications

### Session 2026-10-26

- Q: ¿Se puede registrar un vehículo en un parqueadero inactivo, o trasladarlo a uno? → A: No; se rechaza con "El parqueadero está inactivo". Los vehículos que ya estaban se mantienen y se pueden editar, trasladar fuera o eliminar.
- Q: ¿La búsqueda por placa es exacta o encuentra placas que empiecen con lo escrito? → A: Por inicio de placa, mínimo 2 caracteres, resultados ordenados por placa.
- Q: ¿Un parqueadero puede tener un horario que cruce la medianoche (p. ej. 18:00–02:00)? → A: Sí; si la hora de cierre es menor que la de apertura, cierra al día siguiente.
- Q: ¿Esta primera versión debe exigir inicio de sesión o protección de acceso? → A: No; la autenticación es una funcionalidad separada posterior y, hasta entonces, el sistema no se expone en internet.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Administrar parqueaderos (Priority: P1)

Como administrador, quiero registrar, consultar, modificar y eliminar parqueaderos (nombre,
dirección, ciudad, horario de atención) para tener el catálogo de sedes que opera la empresa.

**Why this priority**: el parqueadero es la entidad raíz; sin él no existen espacios ni
vehículos asociados.

**Independent Test**: crear un parqueadero, verlo en el listado, editar su dirección y
eliminarlo; el catálogo refleja cada cambio.

**Acceptance Scenarios**:

1. **Given** no existe un parqueadero con el nombre "Sede Norte", **When** el administrador lo
   registra con todos los datos obligatorios, **Then** el parqueadero aparece en el listado con
   0 espacios.
2. **Given** un parqueadero existente, **When** el administrador modifica su horario,
   **Then** la consulta del parqueadero muestra el nuevo horario.
3. **Given** un formulario con campos obligatorios vacíos, **When** el administrador intenta
   guardar, **Then** el sistema rechaza la operación e indica qué campos faltan.
4. **Given** un parqueadero con vehículos asociados, **When** el administrador intenta
   eliminarlo, **Then** el sistema impide la eliminación e informa el motivo.

---

### User Story 2 - Administrar espacios de un parqueadero (Priority: P2)

Como administrador, quiero crear y gestionar los espacios de cada parqueadero (código,
tipo de vehículo admitido, estado) para conocer su capacidad y disponibilidad.

**Why this priority**: los espacios definen la capacidad real del parqueadero y son
necesarios para ubicar vehículos.

**Independent Test**: sobre un parqueadero existente, crear espacios de distintos tipos,
cambiar el estado de uno a "fuera de servicio" y consultar la disponibilidad por tipo.

**Acceptance Scenarios**:

1. **Given** un parqueadero existente, **When** el administrador crea el espacio "A-01" para
   automóviles, **Then** el espacio queda "disponible" y la capacidad del parqueadero aumenta
   en 1.
2. **Given** el espacio "A-01" ya existe en ese parqueadero, **When** se intenta crear otro
   espacio con el mismo código, **Then** el sistema lo rechaza por código duplicado.
3. **Given** un parqueadero con espacios, **When** el administrador consulta su resumen,
   **Then** ve el total de espacios y cuántos están disponibles, ocupados y fuera de servicio,
   desglosado por tipo de vehículo.
4. **Given** un espacio ocupado por un vehículo, **When** se intenta eliminarlo,
   **Then** el sistema lo impide hasta que el vehículo sea liberado o reasignado.

---

### User Story 3 - Registrar vehículos del parqueadero y asignarles espacio (Priority: P3)

Como administrador, quiero registrar los vehículos que pertenecen a un parqueadero (placa,
tipo, marca, color, propietario) y asignarles un espacio compatible, para controlar qué
vehículo usa cada espacio.

**Why this priority**: completa el modelo del dominio y depende de parqueaderos y espacios.

**Independent Test**: registrar un vehículo en un parqueadero, asignarle un espacio libre,
verificar que el espacio pase a "ocupado", liberarlo y verificar que vuelva a "disponible".

**Acceptance Scenarios**:

1. **Given** un parqueadero existente, **When** el administrador registra el vehículo con placa
   "ABC123" tipo automóvil, **Then** el vehículo aparece en el listado de vehículos de ese
   parqueadero.
2. **Given** la placa "ABC123" ya está registrada en el sistema, **When** se intenta registrar
   otro vehículo con la misma placa, **Then** el sistema lo rechaza.
3. **Given** un vehículo tipo motocicleta y un espacio disponible para automóviles,
   **When** se intenta asignar ese espacio, **Then** el sistema lo rechaza por tipo
   incompatible.
4. **Given** un vehículo con espacio asignado, **When** el administrador libera la asignación,
   **Then** el espacio queda "disponible" y el vehículo sigue registrado sin espacio.
5. **Given** un parqueadero, **When** se busca por placa, **Then** el sistema muestra el
   vehículo, su parqueadero y su espacio asignado (si tiene).

---

### Edge Cases

- Una placa se ingresa con minúsculas, espacios o guiones ("abc-123"): se normaliza a
  mayúsculas sin separadores antes de validar unicidad.
- Se intenta asignar un espacio que pertenece a otro parqueadero distinto al del vehículo:
  se rechaza.
- Se intenta asignar un espacio "fuera de servicio" u ocupado: se rechaza.
- Se intenta asignar un segundo espacio a un vehículo que ya tiene uno: se rechaza (debe
  liberarse o reasignarse primero).
- Se cambia un espacio ocupado a "fuera de servicio": se rechaza mientras tenga vehículo.
- Dos administradores intentan asignar el mismo espacio libre al mismo tiempo: solo una
  asignación tiene éxito; la otra recibe un mensaje de espacio no disponible.
- Se consulta, modifica o elimina un parqueadero, espacio o vehículo inexistente: el sistema
  responde que el recurso no existe.
- Por un fallo parcial, un espacio queda "ocupado" por un vehículo que ya no existe o que ya no
  lo tiene asignado: el administrador puede forzar su liberación, y el sistema solo la permite
  después de verificar que ningún vehículo tiene asignado ese espacio.

## Requirements *(mandatory)*

### Functional Requirements

**Parqueaderos**

- **FR-001**: El sistema MUST permitir crear, consultar (individual y listado), modificar y
  eliminar parqueaderos con: nombre, dirección, ciudad, horario de atención y estado
  (activo/inactivo). El horario es "abierto 24 h" o una hora de apertura y una de cierre
  distintas; si la de cierre es menor que la de apertura, el parqueadero cierra al día
  siguiente (p. ej. 18:00–02:00 es válido).
- **FR-002**: El nombre del parqueadero MUST ser único dentro de la misma ciudad.
- **FR-003**: El sistema MUST impedir eliminar un parqueadero que tenga espacios o vehículos
  asociados; en su lugar MUST permitir marcarlo como inactivo.
- **FR-004**: El listado de parqueaderos MUST permitir filtrar por ciudad y estado.

**Espacios**

- **FR-005**: El sistema MUST permitir crear, consultar, modificar y eliminar espacios dentro
  de un parqueadero, con: código, tipo de vehículo admitido (automóvil, motocicleta,
  bicicleta) y estado (disponible, ocupado, fuera de servicio).
- **FR-006**: El código del espacio MUST ser único dentro de su parqueadero.
- **FR-007**: El estado "ocupado" MUST ser gestionado únicamente por el sistema al asignar o
  liberar un vehículo; el administrador solo puede alternar entre "disponible" y "fuera de
  servicio".
- **FR-008**: El sistema MUST impedir eliminar un espacio ocupado.
- **FR-009**: El sistema MUST mostrar, por parqueadero, la cantidad de espacios totales,
  disponibles, ocupados y fuera de servicio, desglosada por tipo de vehículo.
- **FR-010**: Los espacios de un parqueadero inactivo MUST NOT poder asignarse.

**Vehículos**

- **FR-011**: El sistema MUST permitir registrar, consultar, modificar y eliminar vehículos
  pertenecientes a un parqueadero, con: placa, tipo (automóvil, motocicleta, bicicleta),
  marca, color y nombre y documento del propietario.
- **FR-012**: La placa MUST normalizarse (mayúsculas, sin espacios ni guiones) y ser única en
  todo el sistema; las bicicletas pueden registrarse con un código identificador en lugar de
  placa.
- **FR-013**: Un vehículo MUST pertenecer a exactamente un parqueadero; el sistema MUST
  permitir trasladarlo a otro parqueadero liberando antes su espacio.
- **FR-013a**: El sistema MUST rechazar registrar un vehículo en un parqueadero inactivo o
  trasladarlo a uno, con el mensaje "El parqueadero está inactivo". Los vehículos que ya
  pertenecían al parqueadero antes de inactivarlo se conservan y se pueden editar, trasladar a
  otro parqueadero o eliminar.
- **FR-014**: El sistema MUST permitir asignar a un vehículo un espacio disponible, del mismo
  parqueadero y compatible con su tipo; al asignarlo el espacio pasa a "ocupado".
- **FR-015**: Un vehículo MUST tener como máximo un espacio asignado y un espacio MUST tener
  como máximo un vehículo.
- **FR-016**: El sistema MUST permitir liberar o reasignar el espacio de un vehículo; al
  liberarlo el espacio vuelve a "disponible".
- **FR-017**: Al eliminar un vehículo con espacio asignado, el sistema MUST liberar el espacio.
- **FR-018**: El sistema MUST permitir buscar vehículos por placa y listar los vehículos de un
  parqueadero, indicando su espacio asignado si existe. La búsqueda por placa MUST normalizar el
  texto y devolver los vehículos cuya placa **empieza** con él (mínimo 2 caracteres; menos →
  rechazo con mensaje), ordenados por placa; la placa completa devuelve exactamente ese vehículo.

**Generales**

- **FR-019**: Toda operación rechazada MUST devolver un mensaje claro que indique el motivo
  (dato faltante, duplicado, recurso inexistente, regla de negocio incumplida).
- **FR-020**: Las validaciones de reglas de negocio MUST ejecutarse antes de guardar cualquier
  cambio, de modo que una operación rechazada no deje datos parciales.

### Key Entities *(include if feature involves data)*

- **Parqueadero**: sede física que agrupa espacios y vehículos. Atributos: nombre, dirección,
  ciudad, horario de atención, estado. Tiene muchos espacios y muchos vehículos.
- **Espacio**: lugar individual dentro de un parqueadero. Atributos: código, tipo de vehículo
  admitido, estado. Pertenece a un parqueadero; puede estar ocupado por a lo sumo un vehículo.
- **Vehículo**: vehículo registrado como perteneciente a un parqueadero. Atributos: placa (o
  código para bicicletas), tipo, marca, color, propietario (nombre y documento). Pertenece a
  un parqueadero; puede tener a lo sumo un espacio asignado.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un administrador puede registrar un parqueadero nuevo con 20 espacios en menos
  de 5 minutos.
- **SC-002**: Registrar un vehículo y asignarle un espacio toma menos de 1 minuto.
- **SC-003**: La búsqueda de un vehículo por placa muestra su parqueadero y espacio en menos de
  2 segundos con al menos 10.000 vehículos registrados.
- **SC-004**: El 100 % de los intentos de asignación inválidos (espacio ocupado, fuera de
  servicio, de otro parqueadero o de tipo incompatible) son rechazados con mensaje explicativo.
- **SC-005**: En todo momento, el resumen de disponibilidad de un parqueadero coincide
  exactamente con el estado individual de sus espacios.
- **SC-006**: El 90 % de los administradores completa el flujo crear parqueadero → crear
  espacio → registrar y ubicar vehículo en el primer intento sin ayuda.

## Assumptions

- "Vehículos que pertenecen al parqueadero" se interpreta como vehículos registrados
  (abonados/residentes) asociados a un parqueadero y, opcionalmente, a un espacio fijo; el
  control de entradas/salidas por visita, tarifas, cobros y reservas queda fuera de alcance y
  se especificará en funcionalidades posteriores.
- Hay un único rol en esta versión: administrador, y **no hay autenticación**; la
  autenticación y los roles se definirán en una funcionalidad aparte, cuyo punto de
  integración será el API Gateway.
- Riesgo conocido: sin autenticación, cualquiera que alcance el gateway puede modificar datos;
  por eso esta versión MUST NOT desplegarse expuesta a internet (solo local o red privada)
  hasta que exista la funcionalidad de autenticación.
- Los tipos de vehículo iniciales son automóvil, motocicleta y bicicleta; un espacio admite
  un solo tipo.
- El propietario se registra como datos del vehículo (nombre y documento), no como entidad
  independiente con cuenta propia.
- La interfaz es una aplicación web para escritorio; el uso desde móvil es deseable pero no
  obligatorio en esta versión.
- No se requiere historial/auditoría de cambios en esta versión.
