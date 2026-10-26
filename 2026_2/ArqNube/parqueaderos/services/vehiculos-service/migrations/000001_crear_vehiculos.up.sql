CREATE TABLE vehiculos (
    id                    uuid         PRIMARY KEY,
    placa                 varchar(10)  NOT NULL,
    tipo                  varchar(20)  NOT NULL CHECK (tipo IN ('AUTOMOVIL', 'MOTOCICLETA', 'BICICLETA')),
    marca                 varchar(50)  NULL,
    color                 varchar(30)  NOT NULL,
    propietario_nombre    varchar(100) NOT NULL,
    propietario_documento varchar(20)  NOT NULL,
    parqueadero_id        uuid         NOT NULL,
    espacio_id            uuid         NULL,
    espacio_codigo        varchar(20)  NULL,
    creado_en             timestamptz  NOT NULL DEFAULT now(),
    actualizado_en        timestamptz  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_vehiculos_placa ON vehiculos (placa);
-- Búsqueda por prefijo de placa (placa LIKE 'X%'), FR-018.
CREATE INDEX ix_vehiculos_placa_prefijo ON vehiculos (placa varchar_pattern_ops);
CREATE INDEX ix_vehiculos_parqueadero ON vehiculos (parqueadero_id);
CREATE UNIQUE INDEX uq_vehiculos_espacio ON vehiculos (espacio_id) WHERE espacio_id IS NOT NULL;
