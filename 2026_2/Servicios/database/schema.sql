-- ============================================================
-- Sistema de Gestión Compra-Venta de Vehículos — Concesionaria
-- DDL PostgreSQL
-- ============================================================

-- Extensiones
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- GU — Gestión de Usuarios y Accesos
-- ============================================================

CREATE TABLE roles (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE permisos (
    id          BIGSERIAL   PRIMARY KEY,
    modulo      VARCHAR(50) NOT NULL,
    accion      VARCHAR(30) NOT NULL,
    descripcion TEXT,
    CONSTRAINT ck_permisos_accion CHECK (accion IN ('LECTURA', 'ESCRITURA', 'APROBACION')),
    CONSTRAINT uq_permisos_modulo_accion UNIQUE (modulo, accion)
);

CREATE TABLE usuarios (
    id                  BIGSERIAL    PRIMARY KEY,
    nombres             VARCHAR(100) NOT NULL,
    apellidos           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    celular             VARCHAR(20),
    password_hash       VARCHAR(255) NOT NULL,
    tipo                VARCHAR(10)  NOT NULL,
    tipo_documento      VARCHAR(10)  NOT NULL,
    numero_documento    VARCHAR(30)  NOT NULL UNIQUE,
    email_verificado    BOOLEAN      NOT NULL DEFAULT FALSE,
    celular_verificado  BOOLEAN      NOT NULL DEFAULT FALSE,
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ,
    CONSTRAINT ck_usuarios_tipo CHECK (tipo IN ('INTERNO', 'COMPRADOR'))
);

CREATE TABLE roles_permisos (
    rol_id     BIGINT NOT NULL REFERENCES roles(id)    ON DELETE CASCADE,
    permiso_id BIGINT NOT NULL REFERENCES permisos(id) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, permiso_id)
);

CREATE TABLE usuarios_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol_id     BIGINT NOT NULL REFERENCES roles(id)    ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

CREATE TABLE tokens_recuperacion (
    id               BIGSERIAL    PRIMARY KEY,
    usuario_id       BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token            VARCHAR(255) NOT NULL UNIQUE,
    usado            BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_expiracion TIMESTAMPTZ  NOT NULL,
    fecha_creacion   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- GV — Gestión de Inventario de Vehículos
-- ============================================================

CREATE TABLE marcas (
    id     BIGSERIAL    PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE modelos (
    id       BIGSERIAL    PRIMARY KEY,
    marca_id BIGINT       NOT NULL REFERENCES marcas(id),
    nombre   VARCHAR(100) NOT NULL,
    CONSTRAINT uq_modelos_marca_nombre UNIQUE (marca_id, nombre)
);

CREATE TABLE vehiculos (
    id                  BIGSERIAL      PRIMARY KEY,
    vin                 VARCHAR(17)    NOT NULL UNIQUE,
    placa               VARCHAR(10)    UNIQUE,
    modelo_id           BIGINT         NOT NULL REFERENCES modelos(id),
    anio                SMALLINT       NOT NULL,
    tipo                VARCHAR(6)     NOT NULL,
    color               VARCHAR(50),
    kilometraje         INTEGER        NOT NULL DEFAULT 0,
    precio_base         NUMERIC(15,2)  NOT NULL,
    estado              VARCHAR(20)    NOT NULL DEFAULT 'INGRESADO',
    descripcion         TEXT,
    publicado_en        TIMESTAMPTZ,
    retirado_en         TIMESTAMPTZ,
    usuario_registra_id BIGINT         NOT NULL REFERENCES usuarios(id),
    fecha_creacion      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ,
    CONSTRAINT ck_vehiculos_tipo       CHECK (tipo IN ('NUEVO', 'USADO')),
    CONSTRAINT ck_vehiculos_estado     CHECK (estado IN ('INGRESADO','EN_VERIFICACION','VERIFICADO','PUBLICADO','RESERVADO','EN_VENTA','VENDIDO','RETIRADO')),
    CONSTRAINT ck_vehiculos_kilometraje CHECK (kilometraje >= 0),
    CONSTRAINT ck_vehiculos_precio     CHECK (precio_base > 0),
    CONSTRAINT ck_vehiculos_anio       CHECK (anio >= 1900 AND anio <= 2100)
);

CREATE TABLE fichas_tecnicas (
    id                  BIGSERIAL    PRIMARY KEY,
    vehiculo_id         BIGINT       NOT NULL UNIQUE REFERENCES vehiculos(id) ON DELETE CASCADE,
    motor               VARCHAR(100),
    transmision         VARCHAR(50),
    combustible         VARCHAR(30),
    cilindraje_cc       INTEGER,
    potencia_hp         INTEGER,
    num_puertas         SMALLINT,
    capacidad_pasajeros SMALLINT,
    num_airbags         SMALLINT,
    url_documento_pdf   VARCHAR(500)
);

CREATE TABLE imagenes_vehiculo (
    id          BIGSERIAL    PRIMARY KEY,
    vehiculo_id BIGINT       NOT NULL REFERENCES vehiculos(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    orden       SMALLINT     NOT NULL DEFAULT 0,
    es_principal BOOLEAN     NOT NULL DEFAULT FALSE,
    fecha_carga TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE documentos_vehiculo (
    id                BIGSERIAL    PRIMARY KEY,
    vehiculo_id       BIGINT       NOT NULL REFERENCES vehiculos(id) ON DELETE CASCADE,
    tipo_documento    VARCHAR(50)  NOT NULL,
    url               VARCHAR(500) NOT NULL,
    fecha_expedicion  DATE,
    fecha_vencimiento DATE,
    validado          BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_validacion  TIMESTAMPTZ,
    usuario_valida_id BIGINT       REFERENCES usuarios(id),
    CONSTRAINT ck_docs_tipo CHECK (tipo_documento IN ('SOAT','TECNOMECANICA','TARJETA_PROPIEDAD','RTM','OTRO'))
);

-- ============================================================
-- DL — Documentación Legal (verificación RUNT — depende de vehiculos)
-- ============================================================

CREATE TABLE verificaciones_runt (
    id                  BIGSERIAL    PRIMARY KEY,
    vehiculo_id         BIGINT       NOT NULL REFERENCES vehiculos(id),
    placa               VARCHAR(10)  NOT NULL,
    tiene_prendas       BOOLEAN,
    tiene_embargos      BOOLEAN,
    reporte_hurto       BOOLEAN,
    estado_legal        VARCHAR(50),
    respuesta_raw       JSONB,
    usuario_consulta_id BIGINT       NOT NULL REFERENCES usuarios(id),
    fecha_consulta      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- BF — Búsqueda y Filtros
-- ============================================================

CREATE TABLE busquedas_guardadas (
    id             BIGSERIAL    PRIMARY KEY,
    usuario_id     BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nombre         VARCHAR(100),
    criterios      JSONB        NOT NULL,
    alerta_activa  BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- CF — Cotización y Financiamiento
-- ============================================================

CREATE TABLE cotizaciones (
    id             BIGSERIAL      PRIMARY KEY,
    codigo         VARCHAR(30)    NOT NULL UNIQUE,
    vehiculo_id    BIGINT         NOT NULL REFERENCES vehiculos(id),
    comprador_id   BIGINT         NOT NULL REFERENCES usuarios(id),
    asesor_id      BIGINT         REFERENCES usuarios(id),
    precio_base    NUMERIC(15,2)  NOT NULL,
    descuento      NUMERIC(15,2)  NOT NULL DEFAULT 0,
    impuestos      NUMERIC(15,2)  NOT NULL DEFAULT 0,
    precio_total   NUMERIC(15,2)  NOT NULL,
    vigencia_hasta DATE           NOT NULL,
    estado         VARCHAR(20)    NOT NULL DEFAULT 'BORRADOR',
    fecha_creacion TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_cotizaciones_estado   CHECK (estado IN ('BORRADOR','ENVIADA','ACEPTADA','RECHAZADA','VENCIDA')),
    CONSTRAINT ck_cotizaciones_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_cotizaciones_total    CHECK (precio_total >= 0)
);

CREATE TABLE accesorios_cotizacion (
    id            BIGSERIAL      PRIMARY KEY,
    cotizacion_id BIGINT         NOT NULL REFERENCES cotizaciones(id) ON DELETE CASCADE,
    descripcion   VARCHAR(200)   NOT NULL,
    precio        NUMERIC(12,2)  NOT NULL,
    CONSTRAINT ck_accesorios_precio CHECK (precio >= 0)
);

CREATE TABLE entidades_financieras (
    id            BIGSERIAL    PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    nit           VARCHAR(20)  NOT NULL UNIQUE,
    endpoint_api  VARCHAR(500),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE solicitudes_credito (
    id                   BIGSERIAL      PRIMARY KEY,
    cotizacion_id        BIGINT         NOT NULL REFERENCES cotizaciones(id),
    comprador_id         BIGINT         NOT NULL REFERENCES usuarios(id),
    entidad_financiera_id BIGINT        NOT NULL REFERENCES entidades_financieras(id),
    monto_solicitado     NUMERIC(15,2)  NOT NULL,
    cuota_inicial        NUMERIC(15,2),
    plazo_meses          SMALLINT       NOT NULL,
    estado               VARCHAR(20)    NOT NULL DEFAULT 'ENVIADA',
    observaciones        TEXT,
    fecha_envio          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    fecha_respuesta      TIMESTAMPTZ,
    CONSTRAINT ck_solicitudes_estado  CHECK (estado IN ('ENVIADA','EN_ESTUDIO','APROBADA','RECHAZADA')),
    CONSTRAINT ck_solicitudes_plazo   CHECK (plazo_meses > 0),
    CONSTRAINT ck_solicitudes_monto   CHECK (monto_solicitado > 0)
);

-- ============================================================
-- PV — Proceso de Venta
-- ============================================================

CREATE TABLE oportunidades_venta (
    id                  BIGSERIAL    PRIMARY KEY,
    vehiculo_id         BIGINT       NOT NULL REFERENCES vehiculos(id),
    comprador_id        BIGINT       NOT NULL REFERENCES usuarios(id),
    asesor_id           BIGINT       REFERENCES usuarios(id),
    estado              VARCHAR(30)  NOT NULL DEFAULT 'NUEVA',
    origen              VARCHAR(20),
    fecha_creacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMPTZ,
    CONSTRAINT ck_oportunidades_estado  CHECK (estado IN ('NUEVA','EN_NEGOCIACION','COTIZACION_ENVIADA','CREDITO_EN_TRAMITE','GANADA','PERDIDA')),
    CONSTRAINT ck_oportunidades_origen  CHECK (origen IN ('WEB','PRESENCIAL','TELEFONO'))
);

CREATE TABLE reservas (
    id             BIGSERIAL      PRIMARY KEY,
    vehiculo_id    BIGINT         NOT NULL UNIQUE REFERENCES vehiculos(id),
    oportunidad_id BIGINT         NOT NULL REFERENCES oportunidades_venta(id),
    comprador_id   BIGINT         NOT NULL REFERENCES usuarios(id),
    asesor_id      BIGINT         REFERENCES usuarios(id),
    monto_reserva  NUMERIC(12,2),
    vigencia_hasta TIMESTAMPTZ    NOT NULL,
    estado         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_reservas_estado CHECK (estado IN ('ACTIVA','EXPIRADA','CANCELADA','CONVERTIDA'))
);

CREATE TABLE negociaciones (
    id                   BIGSERIAL      PRIMARY KEY,
    oportunidad_id       BIGINT         NOT NULL REFERENCES oportunidades_venta(id),
    asesor_id            BIGINT         NOT NULL REFERENCES usuarios(id),
    precio_ofertado      NUMERIC(15,2)  NOT NULL,
    descuento_porcentaje NUMERIC(5,2),
    descuento_valor      NUMERIC(15,2),
    requiere_aprobacion  BOOLEAN        NOT NULL DEFAULT FALSE,
    estado_aprobacion    VARCHAR(20),
    aprobador_id         BIGINT         REFERENCES usuarios(id),
    fecha_aprobacion     TIMESTAMPTZ,
    observaciones        TEXT,
    fecha_creacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_negociaciones_aprobacion CHECK (estado_aprobacion IN ('PENDIENTE','APROBADA','RECHAZADA')),
    CONSTRAINT ck_negociaciones_descuento  CHECK (descuento_porcentaje IS NULL OR (descuento_porcentaje BETWEEN 0 AND 100))
);

CREATE TABLE ventas (
    id                   BIGSERIAL      PRIMARY KEY,
    numero_venta         VARCHAR(30)    NOT NULL UNIQUE,
    oportunidad_id       BIGINT         NOT NULL UNIQUE REFERENCES oportunidades_venta(id),
    vehiculo_id          BIGINT         NOT NULL REFERENCES vehiculos(id),
    comprador_id         BIGINT         NOT NULL REFERENCES usuarios(id),
    asesor_id            BIGINT         NOT NULL REFERENCES usuarios(id),
    precio_final         NUMERIC(15,2)  NOT NULL,
    forma_pago           VARCHAR(10)    NOT NULL,
    solicitud_credito_id BIGINT         REFERENCES solicitudes_credito(id),
    estado               VARCHAR(20)    NOT NULL DEFAULT 'EN_PROCESO',
    fecha_cierre         TIMESTAMPTZ,
    fecha_creacion       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ventas_forma_pago CHECK (forma_pago IN ('CONTADO','CREDITO')),
    CONSTRAINT ck_ventas_estado     CHECK (estado IN ('EN_PROCESO','CERRADA','ANULADA')),
    CONSTRAINT ck_ventas_precio     CHECK (precio_final > 0)
);

-- ============================================================
-- DL — Documentación Legal (contratos y traspasos — dependen de ventas)
-- ============================================================

CREATE TABLE contratos (
    id                BIGSERIAL    PRIMARY KEY,
    venta_id          BIGINT       NOT NULL UNIQUE REFERENCES ventas(id),
    numero_contrato   VARCHAR(50)  NOT NULL UNIQUE,
    url_documento     VARCHAR(500),
    firmado_comprador BOOLEAN      NOT NULL DEFAULT FALSE,
    firmado_asesor    BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_firma       TIMESTAMPTZ,
    fecha_generacion  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE tramites_traspaso (
    id                  BIGSERIAL    PRIMARY KEY,
    venta_id            BIGINT       NOT NULL REFERENCES ventas(id),
    estado              VARCHAR(20)  NOT NULL DEFAULT 'INICIADO',
    observaciones       TEXT,
    usuario_gestiona_id BIGINT       REFERENCES usuarios(id),
    fecha_inicio        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_completado    TIMESTAMPTZ,
    CONSTRAINT ck_tramites_estado CHECK (estado IN ('INICIADO','EN_TRAMITE','EN_TRANSITO','COMPLETADO','RECHAZADO'))
);

-- ============================================================
-- PG — Pagos
-- ============================================================

CREATE TABLE pagos (
    id                BIGSERIAL      PRIMARY KEY,
    venta_id          BIGINT         NOT NULL REFERENCES ventas(id),
    tipo_pago         VARCHAR(30)    NOT NULL,
    monto             NUMERIC(15,2)  NOT NULL,
    medio_pago        VARCHAR(30)    NOT NULL,
    estado            VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE',
    referencia_externa VARCHAR(100),
    fecha_pago        TIMESTAMPTZ,
    fecha_creacion    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_pagos_tipo      CHECK (tipo_pago IN ('CUOTA_INICIAL','SALDO','DESEMBOLSO_CREDITO')),
    CONSTRAINT ck_pagos_medio     CHECK (medio_pago IN ('TRANSFERENCIA','TARJETA','PSE','EFECTIVO','DESEMBOLSO')),
    CONSTRAINT ck_pagos_estado    CHECK (estado IN ('PENDIENTE','PROCESANDO','APROBADO','RECHAZADO')),
    CONSTRAINT ck_pagos_monto     CHECK (monto > 0)
);

CREATE TABLE transacciones_pasarela (
    id                   BIGSERIAL      PRIMARY KEY,
    pago_id              BIGINT         NOT NULL REFERENCES pagos(id),
    proveedor            VARCHAR(50)    NOT NULL,
    referencia_pasarela  VARCHAR(100)   NOT NULL UNIQUE,
    estado_pasarela      VARCHAR(50),
    monto                NUMERIC(15,2),
    respuesta_raw        JSONB,
    fecha_transaccion    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE facturas (
    id              BIGSERIAL      PRIMARY KEY,
    venta_id        BIGINT         NOT NULL UNIQUE REFERENCES ventas(id),
    numero_factura  VARCHAR(50)    NOT NULL UNIQUE,
    cufe            VARCHAR(255)   UNIQUE,
    url_documento   VARCHAR(500),
    subtotal        NUMERIC(15,2)  NOT NULL,
    iva             NUMERIC(15,2)  NOT NULL DEFAULT 0,
    total           NUMERIC(15,2)  NOT NULL,
    estado          VARCHAR(20)    NOT NULL DEFAULT 'GENERADA',
    fecha_emision   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_facturas_estado CHECK (estado IN ('GENERADA','ENVIADA_DIAN','ACEPTADA','RECHAZADA')),
    CONSTRAINT ck_facturas_total  CHECK (total >= 0)
);

-- ============================================================
-- PS — Postventa
-- ============================================================

CREATE TABLE garantias (
    id              BIGSERIAL    PRIMARY KEY,
    venta_id        BIGINT       NOT NULL UNIQUE REFERENCES ventas(id),
    tipo            VARCHAR(20)  NOT NULL,
    descripcion     TEXT,
    duracion_meses  SMALLINT     NOT NULL,
    fecha_inicio    DATE         NOT NULL,
    fecha_fin       DATE         NOT NULL,
    condiciones     TEXT,
    CONSTRAINT ck_garantias_tipo     CHECK (tipo IN ('FABRICA','CONCESIONARIA')),
    CONSTRAINT ck_garantias_duracion CHECK (duracion_meses > 0),
    CONSTRAINT ck_garantias_fechas   CHECK (fecha_fin > fecha_inicio)
);

CREATE TABLE citas_servicio (
    id             BIGSERIAL    PRIMARY KEY,
    comprador_id   BIGINT       NOT NULL REFERENCES usuarios(id),
    vehiculo_id    BIGINT       NOT NULL REFERENCES vehiculos(id),
    asesor_id      BIGINT       REFERENCES usuarios(id),
    tipo_servicio  VARCHAR(100) NOT NULL,
    fecha_cita     TIMESTAMPTZ  NOT NULL,
    estado         VARCHAR(20)  NOT NULL DEFAULT 'AGENDADA',
    observaciones  TEXT,
    fecha_creacion TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_citas_estado CHECK (estado IN ('AGENDADA','CONFIRMADA','REALIZADA','CANCELADA'))
);

CREATE TABLE pqrs (
    id                  BIGSERIAL    PRIMARY KEY,
    numero_radicado     VARCHAR(30)  NOT NULL UNIQUE,
    comprador_id        BIGINT       NOT NULL REFERENCES usuarios(id),
    tipo                VARCHAR(20)  NOT NULL,
    asunto              VARCHAR(200) NOT NULL,
    descripcion         TEXT         NOT NULL,
    venta_id            BIGINT       REFERENCES ventas(id),
    estado              VARCHAR(20)  NOT NULL DEFAULT 'RADICADA',
    usuario_asignado_id BIGINT       REFERENCES usuarios(id),
    respuesta           TEXT,
    fecha_creacion      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    fecha_cierre        TIMESTAMPTZ,
    CONSTRAINT ck_pqrs_tipo   CHECK (tipo IN ('PETICION','QUEJA','RECLAMO','SUGERENCIA')),
    CONSTRAINT ck_pqrs_estado CHECK (estado IN ('RADICADA','EN_GESTION','RESUELTA','CERRADA'))
);

-- ============================================================
-- NT — Notificaciones
-- ============================================================

CREATE TABLE notificaciones (
    id               BIGSERIAL    PRIMARY KEY,
    usuario_id       BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    tipo             VARCHAR(50)  NOT NULL,
    titulo           VARCHAR(200) NOT NULL,
    cuerpo           TEXT,
    canal            VARCHAR(10)  NOT NULL,
    leida            BOOLEAN      NOT NULL DEFAULT FALSE,
    referencia_tipo  VARCHAR(50),
    referencia_id    BIGINT,
    fecha_envio      TIMESTAMPTZ,
    fecha_creacion   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_notificaciones_canal CHECK (canal IN ('EMAIL','SMS','PUSH','IN_APP'))
);

-- ============================================================
-- RA — Reportes y Administración
-- ============================================================

CREATE TABLE auditoria (
    id               BIGSERIAL    PRIMARY KEY,
    usuario_id       BIGINT       REFERENCES usuarios(id),
    accion           VARCHAR(100) NOT NULL,
    entidad          VARCHAR(50)  NOT NULL,
    entidad_id       BIGINT,
    datos_anteriores JSONB,
    datos_nuevos     JSONB,
    ip_origen        VARCHAR(45),
    user_agent       TEXT,
    fecha            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE parametros_sistema (
    id                   BIGSERIAL    PRIMARY KEY,
    clave                VARCHAR(100) NOT NULL UNIQUE,
    valor                TEXT         NOT NULL,
    tipo_dato            VARCHAR(10)  NOT NULL DEFAULT 'STRING',
    descripcion          TEXT,
    usuario_actualiza_id BIGINT       REFERENCES usuarios(id),
    fecha_actualizacion  TIMESTAMPTZ,
    CONSTRAINT ck_parametros_tipo_dato CHECK (tipo_dato IN ('STRING','NUMBER','BOOLEAN','JSON'))
);

-- ============================================================
-- ÍNDICES
-- ============================================================

-- usuarios
CREATE INDEX idx_usuarios_email            ON usuarios(email);
CREATE INDEX idx_usuarios_numero_documento ON usuarios(numero_documento);
CREATE INDEX idx_usuarios_tipo             ON usuarios(tipo);
CREATE INDEX idx_usuarios_activo           ON usuarios(activo);

-- tokens_recuperacion
CREATE INDEX idx_tokens_usuario_id ON tokens_recuperacion(usuario_id);
CREATE INDEX idx_tokens_token      ON tokens_recuperacion(token);

-- modelos
CREATE INDEX idx_modelos_marca_id ON modelos(marca_id);

-- vehiculos
CREATE INDEX idx_vehiculos_modelo_id  ON vehiculos(modelo_id);
CREATE INDEX idx_vehiculos_estado     ON vehiculos(estado);
CREATE INDEX idx_vehiculos_tipo       ON vehiculos(tipo);
CREATE INDEX idx_vehiculos_anio       ON vehiculos(anio);
CREATE INDEX idx_vehiculos_precio     ON vehiculos(precio_base);

-- imagenes_vehiculo
CREATE INDEX idx_imagenes_vehiculo_id ON imagenes_vehiculo(vehiculo_id);

-- documentos_vehiculo
CREATE INDEX idx_docs_vehiculo_id ON documentos_vehiculo(vehiculo_id);

-- verificaciones_runt
CREATE INDEX idx_runt_vehiculo_id ON verificaciones_runt(vehiculo_id);

-- busquedas_guardadas
CREATE INDEX idx_busquedas_usuario_id ON busquedas_guardadas(usuario_id);
CREATE INDEX idx_busquedas_criterios  ON busquedas_guardadas USING GIN (criterios);

-- cotizaciones
CREATE INDEX idx_cotizaciones_vehiculo_id  ON cotizaciones(vehiculo_id);
CREATE INDEX idx_cotizaciones_comprador_id ON cotizaciones(comprador_id);
CREATE INDEX idx_cotizaciones_estado       ON cotizaciones(estado);

-- accesorios_cotizacion
CREATE INDEX idx_accesorios_cotizacion_id ON accesorios_cotizacion(cotizacion_id);

-- solicitudes_credito
CREATE INDEX idx_solicitudes_comprador_id  ON solicitudes_credito(comprador_id);
CREATE INDEX idx_solicitudes_cotizacion_id ON solicitudes_credito(cotizacion_id);
CREATE INDEX idx_solicitudes_estado        ON solicitudes_credito(estado);

-- oportunidades_venta
CREATE INDEX idx_oportunidades_vehiculo_id  ON oportunidades_venta(vehiculo_id);
CREATE INDEX idx_oportunidades_comprador_id ON oportunidades_venta(comprador_id);
CREATE INDEX idx_oportunidades_asesor_id    ON oportunidades_venta(asesor_id);
CREATE INDEX idx_oportunidades_estado       ON oportunidades_venta(estado);

-- negociaciones
CREATE INDEX idx_negociaciones_oportunidad_id ON negociaciones(oportunidad_id);

-- ventas
CREATE INDEX idx_ventas_vehiculo_id  ON ventas(vehiculo_id);
CREATE INDEX idx_ventas_comprador_id ON ventas(comprador_id);
CREATE INDEX idx_ventas_asesor_id    ON ventas(asesor_id);
CREATE INDEX idx_ventas_estado       ON ventas(estado);
CREATE INDEX idx_ventas_fecha_cierre ON ventas(fecha_cierre);

-- pagos
CREATE INDEX idx_pagos_venta_id ON pagos(venta_id);
CREATE INDEX idx_pagos_estado   ON pagos(estado);

-- citas_servicio
CREATE INDEX idx_citas_comprador_id ON citas_servicio(comprador_id);
CREATE INDEX idx_citas_fecha_cita   ON citas_servicio(fecha_cita);
CREATE INDEX idx_citas_estado       ON citas_servicio(estado);

-- pqrs
CREATE INDEX idx_pqrs_comprador_id ON pqrs(comprador_id);
CREATE INDEX idx_pqrs_estado       ON pqrs(estado);

-- notificaciones
CREATE INDEX idx_notificaciones_usuario_id ON notificaciones(usuario_id);
CREATE INDEX idx_notificaciones_leida      ON notificaciones(usuario_id, leida);

-- auditoria
CREATE INDEX idx_auditoria_usuario_id ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_entidad    ON auditoria(entidad, entidad_id);
CREATE INDEX idx_auditoria_fecha      ON auditoria(fecha DESC);
