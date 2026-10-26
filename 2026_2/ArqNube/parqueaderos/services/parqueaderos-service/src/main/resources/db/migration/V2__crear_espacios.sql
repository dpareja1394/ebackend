CREATE TABLE espacios (
    id             uuid        PRIMARY KEY,
    parqueadero_id uuid        NOT NULL REFERENCES parqueaderos (id),
    codigo         varchar(20) NOT NULL,
    tipo_vehiculo  varchar(20) NOT NULL CHECK (tipo_vehiculo IN ('AUTOMOVIL', 'MOTOCICLETA', 'BICICLETA')),
    estado         varchar(20) NOT NULL DEFAULT 'DISPONIBLE'
                               CHECK (estado IN ('DISPONIBLE', 'OCUPADO', 'FUERA_DE_SERVICIO')),
    vehiculo_id    uuid        NULL,
    creado_en      timestamptz NOT NULL,
    actualizado_en timestamptz NOT NULL,
    -- Un espacio tiene vehículo si y solo si está ocupado.
    CONSTRAINT ck_espacios_ocupacion CHECK ((estado = 'OCUPADO') = (vehiculo_id IS NOT NULL))
);

CREATE UNIQUE INDEX uq_espacios_parqueadero_codigo ON espacios (parqueadero_id, codigo);
CREATE INDEX ix_espacios_resumen ON espacios (parqueadero_id, estado, tipo_vehiculo);
-- vehiculo_id NO es único a propósito: al reasignar, el vehículo ocupa el espacio nuevo antes de liberar
-- el anterior (research.md R2). "Un vehículo, a lo sumo un espacio" (FR-015) lo garantiza vehiculos-service,
-- que guarda un único espacio por vehículo.
CREATE INDEX ix_espacios_vehiculo ON espacios (vehiculo_id) WHERE vehiculo_id IS NOT NULL;
