# Data Model: Gestión de Parqueaderos, Espacios y Vehículos

**Feature**: `001-gestion-parqueaderos` | **Date**: 2026-10-26

Dos bases de datos independientes (Principio I). Las referencias entre servicios son solo
identificadores, sin llaves foráneas físicas.

## Base `parqueaderos` (dueño: `parqueaderos-service`)

### Parqueadero

| Campo | Tipo | Reglas |
|-------|------|--------|
| `id` | UUID | PK, generado por el sistema |
| `nombre` | texto (3–100) | obligatorio; único con `ciudad` sin distinguir mayúsculas (FR-002) |
| `direccion` | texto (5–200) | obligatorio |
| `ciudad` | texto (2–80) | obligatorio |
| `abierto24h` | booleano | obligatorio, por defecto `false` |
| `horaApertura` | hora (HH:mm) | obligatoria si `abierto24h = false` |
| `horaCierre` | hora (HH:mm) | obligatoria si `abierto24h = false`; distinta de `horaApertura`; si es menor que `horaApertura`, cierra al día siguiente |
| `estado` | enum `ACTIVO` \| `INACTIVO` | por defecto `ACTIVO` |
| `creadoEn`, `actualizadoEn` | timestamp | gestionados por el sistema |

Reglas:
- No se puede eliminar si tiene espacios o vehículos (FR-003) → `409`.
- Inactivo: sus espacios no pueden asignarse (FR-010).
- Índice único: `(lower(nombre), lower(ciudad))`. Índices: `ciudad`, `estado`.

### Espacio

| Campo | Tipo | Reglas |
|-------|------|--------|
| `id` | UUID | PK |
| `parqueaderoId` | UUID | FK → Parqueadero (misma base), obligatorio, inmutable |
| `codigo` | texto (1–20), mayúsculas | obligatorio; único por parqueadero (FR-006); no editable si está `OCUPADO` |
| `tipoVehiculo` | enum `AUTOMOVIL` \| `MOTOCICLETA` \| `BICICLETA` | obligatorio; no editable si está `OCUPADO` |
| `estado` | enum `DISPONIBLE` \| `OCUPADO` \| `FUERA_DE_SERVICIO` | por defecto `DISPONIBLE` |
| `vehiculoId` | UUID, nulo | presente sí y solo sí `estado = OCUPADO` |
| `creadoEn`, `actualizadoEn` | timestamp | |

Índice único: `(parqueadero_id, codigo)`. Índice: `(parqueadero_id, estado, tipo_vehiculo)`
para el resumen. Restricción `CHECK ((estado = 'OCUPADO') = (vehiculo_id IS NOT NULL))`.
Índice parcial (no único) sobre `vehiculo_id`: al reasignar, el vehículo ocupa el espacio nuevo antes de liberar el anterior (R2), así que por un instante figura en dos espacios. FR-015 lo garantiza el lado de vehículos (un único `espacioId` por vehículo).

#### Transiciones de estado del Espacio

```text
            admin                      sistema (asignar)
DISPONIBLE ───────▶ FUERA_DE_SERVICIO   DISPONIBLE ─────────▶ OCUPADO
           ◀───────                                ◀─────────
            admin                      sistema (liberar)
```

- `DISPONIBLE ↔ FUERA_DE_SERVICIO`: solo por el administrador (FR-007).
- `DISPONIBLE → OCUPADO`: solo vía API interna, con `UPDATE ... WHERE estado = 'DISPONIBLE'`
  (atómico; si 0 filas afectadas → `409`).
- `OCUPADO → DISPONIBLE`: vía API interna (liberación idempotente), o por **liberación forzada**
  del administrador, solo si el sistema verifica que el vehículo registrado en el espacio ya no
  existe o ya no lo referencia (espacio huérfano por un fallo parcial entre servicios).
- `OCUPADO → FUERA_DE_SERVICIO` y eliminar un espacio `OCUPADO`: prohibido (`409`, FR-008).

### Resumen de disponibilidad (vista calculada, no persistida)

Por parqueadero y por `tipoVehiculo`: `total`, `disponibles`, `ocupados`, `fueraDeServicio`,
más los totales generales (FR-009). Se calcula con `GROUP BY` sobre Espacio, por lo que
siempre coincide con el estado individual (SC-005).

## Base `vehiculos` (dueño: `vehiculos-service`)

### Vehículo

| Campo | Tipo | Reglas |
|-------|------|--------|
| `id` | UUID | PK |
| `placa` | texto (3–10) | obligatorio; normalizado a mayúsculas sin espacios ni guiones; alfanumérico; único global (FR-012). Para bicicletas contiene su código identificador |
| `tipo` | enum `AUTOMOVIL` \| `MOTOCICLETA` \| `BICICLETA` | obligatorio |
| `marca` | texto (1–50) | obligatorio salvo `BICICLETA` (opcional) |
| `color` | texto (1–30) | obligatorio |
| `propietarioNombre` | texto (3–100) | obligatorio |
| `propietarioDocumento` | texto (5–20) | obligatorio |
| `parqueaderoId` | UUID | obligatorio; debe existir en `parqueaderos-service` y estar `ACTIVO` al registrar o trasladar (FR-013, FR-013a) |
| `espacioId` | UUID, nulo | espacio asignado (FR-014/015) |
| `espacioCodigo` | texto, nulo | copia del código del espacio al asignar (para listar sin llamada remota) |
| `creadoEn`, `actualizadoEn` | timestamp | |

Índice único: `placa`; índice `varchar_pattern_ops` sobre `placa` para búsqueda por prefijo (FR-018). Índices: `parqueadero_id`. Índice único parcial: `espacio_id`.

Reglas:
- `tipo` no puede cambiarse mientras tenga espacio asignado.
- Asignar con espacio ya asignado → `409` (debe usarse reasignar o liberar).
- Trasladar a otro parqueadero libera primero el espacio (FR-013).
- Eliminar con espacio asignado libera el espacio antes de borrar (FR-017).

## Relaciones

```text
Parqueadero 1 ──── * Espacio            (misma base, FK)
Parqueadero 1 ──── * Vehículo           (entre servicios, por parqueaderoId)
Espacio     0..1 ── 0..1 Vehículo       (entre servicios: Espacio.vehiculoId ↔ Vehiculo.espacioId)
```

El vínculo Espacio ↔ Vehículo se mantiene en ambos lados mediante la saga descrita en
[research.md](./research.md#r2-consistencia-de-la-asignación-vehículo--espacio-entre-servicios).
