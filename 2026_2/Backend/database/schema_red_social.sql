-- ============================================================
-- Esquema: Red Social (basado en req/req3.md)
-- Motor objetivo: PostgreSQL 17
-- Entidades: usuarios, perfiles, relaciones, publicaciones,
-- medios, etiquetas, reacciones, comentarios, notificaciones,
-- bloqueos, denuncias, actividad_historial
-- ============================================================

-- Extensión requerida para índices de búsqueda por similitud de texto (RF-10)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ------------------------------------------------------------
-- Tipos enumerados
-- ------------------------------------------------------------
CREATE TYPE visibilidad_perfil AS ENUM ('publico', 'privado');
CREATE TYPE privacidad_publicacion AS ENUM ('publico', 'seguidores', 'privado');
CREATE TYPE estado_relacion AS ENUM ('pendiente', 'aceptada', 'rechazada');
CREATE TYPE tipo_reaccion AS ENUM ('like', 'dislike');
CREATE TYPE tipo_notificacion AS ENUM (
    'nuevo_seguidor',
    'solicitud_seguimiento',
    'like',
    'dislike',
    'comentario',
    'respuesta_comentario'
);
CREATE TYPE tipo_medio AS ENUM ('imagen', 'video');
CREATE TYPE estado_denuncia AS ENUM ('pendiente', 'revisada', 'descartada');

-- ------------------------------------------------------------
-- Usuarios (RF-01)
-- ------------------------------------------------------------
CREATE TABLE usuarios (
    id             SERIAL PRIMARY KEY,
    email          VARCHAR(150) NOT NULL,
    username       VARCHAR(50)  NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    last_login     TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_usuarios_email UNIQUE (email),
    CONSTRAINT uq_usuarios_username UNIQUE (username)
);

-- ------------------------------------------------------------
-- Perfiles (RF-02)
-- ------------------------------------------------------------
CREATE TABLE perfiles (
    id           SERIAL PRIMARY KEY,
    usuario_id   INTEGER NOT NULL,
    nombre       VARCHAR(150),
    bio          VARCHAR(500),
    avatar_url   VARCHAR(500),
    ubicacion    VARCHAR(150),
    enlace       VARCHAR(255),
    visibilidad  visibilidad_perfil NOT NULL DEFAULT 'publico',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_perfiles_usuario_id UNIQUE (usuario_id),
    CONSTRAINT fk_perfiles_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Relaciones: seguir / amistad (RF-03)
-- ------------------------------------------------------------
CREATE TABLE relaciones (
    id           SERIAL PRIMARY KEY,
    seguidor_id  INTEGER NOT NULL,
    seguido_id   INTEGER NOT NULL,
    estado       estado_relacion NOT NULL DEFAULT 'aceptada',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_relaciones_par UNIQUE (seguidor_id, seguido_id),
    CONSTRAINT ck_relaciones_no_auto_seguimiento CHECK (seguidor_id <> seguido_id),
    CONSTRAINT fk_relaciones_seguidor FOREIGN KEY (seguidor_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_relaciones_seguido FOREIGN KEY (seguido_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Publicaciones (RF-04, RF-05, RF-06)
-- ------------------------------------------------------------
CREATE TABLE publicaciones (
    id          SERIAL PRIMARY KEY,
    autor_id    INTEGER NOT NULL,
    contenido   TEXT,
    privacidad  privacidad_publicacion NOT NULL DEFAULT 'publico',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    CONSTRAINT fk_publicaciones_autor FOREIGN KEY (autor_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Medios de una publicación (RF-04, RF-12)
-- ------------------------------------------------------------
CREATE TABLE medios (
    id             SERIAL PRIMARY KEY,
    publicacion_id INTEGER NOT NULL,
    tipo           tipo_medio NOT NULL DEFAULT 'imagen',
    url_original   VARCHAR(500) NOT NULL,
    url_thumbnail  VARCHAR(500),
    formato        VARCHAR(20),
    tamano_bytes   INTEGER,
    orden          SMALLINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_medios_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Etiquetas / hashtags (RF-04, RF-10)
-- ------------------------------------------------------------
CREATE TABLE etiquetas (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_etiquetas_nombre UNIQUE (nombre)
);

CREATE TABLE publicacion_etiquetas (
    id             SERIAL PRIMARY KEY,
    publicacion_id INTEGER NOT NULL,
    etiqueta_id    INTEGER NOT NULL,
    CONSTRAINT uq_publicacion_etiquetas UNIQUE (publicacion_id, etiqueta_id),
    CONSTRAINT fk_publicacion_etiquetas_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_publicacion_etiquetas_etiqueta FOREIGN KEY (etiqueta_id)
        REFERENCES etiquetas (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Reacciones: like / dislike (RF-07)
-- ------------------------------------------------------------
CREATE TABLE reacciones (
    id             SERIAL PRIMARY KEY,
    publicacion_id INTEGER NOT NULL,
    usuario_id     INTEGER NOT NULL,
    tipo           tipo_reaccion NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reacciones_publicacion_usuario UNIQUE (publicacion_id, usuario_id),
    CONSTRAINT fk_reacciones_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_reacciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Comentarios con respuestas anidadas de 1 nivel (RF-08)
-- ------------------------------------------------------------
CREATE TABLE comentarios (
    id                  SERIAL PRIMARY KEY,
    publicacion_id      INTEGER NOT NULL,
    usuario_id          INTEGER NOT NULL,
    comentario_padre_id INTEGER,
    contenido           VARCHAR(1000) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP,
    CONSTRAINT fk_comentarios_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_comentarios_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_comentarios_padre FOREIGN KEY (comentario_padre_id)
        REFERENCES comentarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Notificaciones (RF-09)
-- ------------------------------------------------------------
CREATE TABLE notificaciones (
    id                SERIAL PRIMARY KEY,
    usuario_id        INTEGER NOT NULL,
    tipo              tipo_notificacion NOT NULL,
    origen_usuario_id INTEGER,
    publicacion_id    INTEGER,
    comentario_id     INTEGER,
    leida             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notificaciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_notificaciones_origen_usuario FOREIGN KEY (origen_usuario_id)
        REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT fk_notificaciones_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_notificaciones_comentario FOREIGN KEY (comentario_id)
        REFERENCES comentarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Bloqueos (RF-11)
-- ------------------------------------------------------------
CREATE TABLE bloqueos (
    id                   SERIAL PRIMARY KEY,
    usuario_id           INTEGER NOT NULL,
    usuario_bloqueado_id INTEGER NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_bloqueos_par UNIQUE (usuario_id, usuario_bloqueado_id),
    CONSTRAINT ck_bloqueos_no_auto_bloqueo CHECK (usuario_id <> usuario_bloqueado_id),
    CONSTRAINT fk_bloqueos_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_bloqueos_usuario_bloqueado FOREIGN KEY (usuario_bloqueado_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Denuncias / reportes (RF-11)
-- ------------------------------------------------------------
CREATE TABLE denuncias (
    id                      SERIAL PRIMARY KEY,
    usuario_reportante_id   INTEGER NOT NULL,
    usuario_reportado_id    INTEGER,
    publicacion_id          INTEGER,
    comentario_id           INTEGER,
    motivo                  VARCHAR(255) NOT NULL,
    estado                  estado_denuncia NOT NULL DEFAULT 'pendiente',
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_denuncias_reportante FOREIGN KEY (usuario_reportante_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncias_reportado FOREIGN KEY (usuario_reportado_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncias_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncias_comentario FOREIGN KEY (comentario_id)
        REFERENCES comentarios (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Historial de actividad (RF-13)
-- ------------------------------------------------------------
CREATE TABLE actividad_historial (
    id              SERIAL PRIMARY KEY,
    usuario_id      INTEGER NOT NULL,
    tipo_actividad  VARCHAR(100) NOT NULL,
    descripcion     VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_actividad_historial_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ============================================================
-- Índices adicionales para búsquedas y llaves foráneas frecuentes
-- ============================================================

-- Relaciones (RF-03, RF-06): resolver seguidores/seguidos y su estado
CREATE INDEX idx_relaciones_seguidor ON relaciones (seguidor_id, estado);
CREATE INDEX idx_relaciones_seguido ON relaciones (seguido_id, estado);

-- Publicaciones (RF-05, RF-06): feed por autor y orden cronológico
CREATE INDEX idx_publicaciones_autor ON publicaciones (autor_id);
CREATE INDEX idx_publicaciones_created_at ON publicaciones (created_at DESC);

-- Medios (RF-04, RF-12)
CREATE INDEX idx_medios_publicacion ON medios (publicacion_id);

-- Etiquetas (RF-10): join hashtag -> publicaciones
CREATE INDEX idx_publicacion_etiquetas_etiqueta ON publicacion_etiquetas (etiqueta_id);

-- Reacciones (RF-07): conteo de likes/dislikes por publicación
CREATE INDEX idx_reacciones_publicacion ON reacciones (publicacion_id, tipo);

-- Comentarios (RF-08): listar por publicación y resolver respuestas anidadas
CREATE INDEX idx_comentarios_publicacion ON comentarios (publicacion_id);
CREATE INDEX idx_comentarios_padre ON comentarios (comentario_padre_id);

-- Notificaciones (RF-09): bandeja del usuario ordenada, filtrando no leídas
CREATE INDEX idx_notificaciones_usuario ON notificaciones (usuario_id, leida, created_at DESC);

-- Bloqueos (RF-11): verificar bloqueo en ambos sentidos
CREATE INDEX idx_bloqueos_usuario_bloqueado ON bloqueos (usuario_bloqueado_id);

-- Denuncias (RF-11): cola de moderación por estado
CREATE INDEX idx_denuncias_estado ON denuncias (estado);

-- Actividad (RF-13): historial por usuario ordenado por fecha
CREATE INDEX idx_actividad_historial_usuario ON actividad_historial (usuario_id, created_at DESC);

-- Búsqueda (RF-10): búsqueda por similitud de texto en publicaciones y username
CREATE INDEX idx_publicaciones_contenido_trgm ON publicaciones USING GIN (contenido gin_trgm_ops);
CREATE INDEX idx_usuarios_username_trgm ON usuarios USING GIN (username gin_trgm_ops);
