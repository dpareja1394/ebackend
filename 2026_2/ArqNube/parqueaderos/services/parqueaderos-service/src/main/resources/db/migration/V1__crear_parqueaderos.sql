CREATE TABLE parqueaderos (
    id             uuid         PRIMARY KEY,
    nombre         varchar(100) NOT NULL,
    direccion      varchar(200) NOT NULL,
    ciudad         varchar(80)  NOT NULL,
    abierto24h     boolean      NOT NULL DEFAULT false,
    hora_apertura  time         NULL,
    hora_cierre    time         NULL,
    estado         varchar(10)  NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    creado_en      timestamptz  NOT NULL,
    actualizado_en timestamptz  NOT NULL,
    -- Si no abre 24 h, ambas horas son obligatorias y distintas.
    -- Cierre < apertura es válido: cierra al día siguiente (p. ej. 18:00–02:00).
    CONSTRAINT ck_parqueaderos_horario CHECK (
        abierto24h OR (hora_apertura IS NOT NULL AND hora_cierre IS NOT NULL AND hora_apertura <> hora_cierre)
    )
);

CREATE UNIQUE INDEX uq_parqueadero_nombre_ciudad ON parqueaderos (lower(nombre), lower(ciudad));
CREATE INDEX ix_parqueaderos_ciudad ON parqueaderos (ciudad);
CREATE INDEX ix_parqueaderos_estado ON parqueaderos (estado);
