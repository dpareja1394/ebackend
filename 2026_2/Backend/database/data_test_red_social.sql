-- ============================================================
-- Datos de prueba: Red Social (dnetwork-java)
-- Motor: PostgreSQL 17
-- Requiere haber ejecutado antes: schema_red_social.sql
--
-- Los INSERT usan subconsultas por columnas UNIQUE (email, username,
-- nombre de etiqueta, etc.) en lugar de IDs fijos, para que el script
-- se pueda volver a ejecutar sin depender del valor actual de las
-- secuencias SERIAL. Es seguro correrlo varias veces: usa
-- ON CONFLICT DO NOTHING en todas las tablas con restricción UNIQUE.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1. usuarios
-- ------------------------------------------------------------
INSERT INTO usuarios (email, username, password_hash, last_login, created_at, updated_at) VALUES
    ('ana.gomez@example.com',    'ana_gomez',    '$2a$10$abcdefghijklmnopqrstuv1', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '90 days', NOW() - INTERVAL '2 hours'),
    ('carlos.ruiz@example.com',  'carlos_ruiz',  '$2a$10$abcdefghijklmnopqrstuv2', NOW() - INTERVAL '1 day',  NOW() - INTERVAL '80 days', NOW() - INTERVAL '1 day'),
    ('laura.diaz@example.com',   'laura_diaz',   '$2a$10$abcdefghijklmnopqrstuv3', NOW() - INTERVAL '5 hours', NOW() - INTERVAL '70 days', NOW() - INTERVAL '5 hours'),
    ('juan.perez@example.com',   'juan_perez',   '$2a$10$abcdefghijklmnopqrstuv4', NOW() - INTERVAL '3 days', NOW() - INTERVAL '60 days', NOW() - INTERVAL '3 days'),
    ('maria.lopez@example.com',  'maria_lopez',  '$2a$10$abcdefghijklmnopqrstuv5', NOW() - INTERVAL '10 hours', NOW() - INTERVAL '50 days', NOW() - INTERVAL '10 hours'),
    ('pedro.sanchez@example.com','pedro_sanchez','$2a$10$abcdefghijklmnopqrstuv6', NULL,                       NOW() - INTERVAL '40 days', NOW() - INTERVAL '40 days'),
    ('sofia.martin@example.com', 'sofia_martin', '$2a$10$abcdefghijklmnopqrstuv7', NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '20 days', NOW() - INTERVAL '30 minutes'),
    ('spam.bot@example.com',     'spam_bot99',   '$2a$10$abcdefghijklmnopqrstuv8', NOW() - INTERVAL '4 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days')
ON CONFLICT (email) DO NOTHING;

-- ------------------------------------------------------------
-- 2. perfiles (1 a 1 con usuarios)
-- ------------------------------------------------------------
INSERT INTO perfiles (usuario_id, nombre, bio, avatar_url, ubicacion, enlace, visibilidad, created_at, updated_at)
SELECT u.id, v.nombre, v.bio, v.avatar_url, v.ubicacion, v.enlace, v.visibilidad::visibilidad_perfil, NOW() - INTERVAL '90 days', NOW()
FROM (VALUES
    ('ana_gomez',     'Ana Gómez',     'Amante de la fotografía y los viajes.',      'https://cdn.example.com/avatars/ana.jpg',     'Cali, Colombia',   'https://anagomez.dev',    'publico'),
    ('carlos_ruiz',   'Carlos Ruiz',   'Desarrollador backend. Java y PostgreSQL.',  'https://cdn.example.com/avatars/carlos.jpg',  'Bogotá, Colombia', NULL,                       'publico'),
    ('laura_diaz',    'Laura Díaz',    'Diseñadora UX/UI.',                          'https://cdn.example.com/avatars/laura.jpg',   'Medellín, Colombia', 'https://laura.design',  'privado'),
    ('juan_perez',    'Juan Pérez',    NULL,                                         NULL,                                            'Cali, Colombia',   NULL,                       'publico'),
    ('maria_lopez',   'María López',   'Estudiante de ingeniería de sistemas.',      'https://cdn.example.com/avatars/maria.jpg',   'Cali, Colombia',   NULL,                       'publico'),
    ('pedro_sanchez', 'Pedro Sánchez', 'Cuenta privada.',                            NULL,                                            NULL,               NULL,                       'privado'),
    ('sofia_martin',  'Sofía Martín',  'Marketing digital y contenido.',             'https://cdn.example.com/avatars/sofia.jpg',   'Cali, Colombia',   'https://sofiamartin.co',  'publico'),
    ('spam_bot99',    'Bot',           'cuenta reportada por spam',                  NULL,                                            NULL,               'https://bit.ly/spamlink', 'publico')
) AS v(username, nombre, bio, avatar_url, ubicacion, enlace, visibilidad)
JOIN usuarios u ON u.username = v.username
ON CONFLICT (usuario_id) DO NOTHING;

-- ------------------------------------------------------------
-- 3. relaciones (seguir / amistad)
-- ------------------------------------------------------------
INSERT INTO relaciones (seguidor_id, seguido_id, estado, created_at, updated_at)
SELECT s.id, d.id, v.estado::estado_relacion, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'
FROM (VALUES
    ('ana_gomez',     'carlos_ruiz',   'aceptada'),
    ('ana_gomez',     'laura_diaz',    'aceptada'),
    ('carlos_ruiz',   'ana_gomez',     'aceptada'),
    ('laura_diaz',    'ana_gomez',     'pendiente'),
    ('juan_perez',    'ana_gomez',     'aceptada'),
    ('maria_lopez',   'sofia_martin',  'aceptada'),
    ('sofia_martin',  'maria_lopez',   'aceptada'),
    ('pedro_sanchez', 'ana_gomez',     'rechazada'),
    ('spam_bot99',    'ana_gomez',     'pendiente')
) AS v(seguidor_username, seguido_username, estado)
JOIN usuarios s ON s.username = v.seguidor_username
JOIN usuarios d ON d.username = v.seguido_username
ON CONFLICT (seguidor_id, seguido_id) DO NOTHING;

-- ------------------------------------------------------------
-- 4. publicaciones
-- ------------------------------------------------------------
INSERT INTO publicaciones (autor_id, contenido, privacidad, created_at, updated_at, deleted_at)
SELECT u.id, v.contenido, v.privacidad::privacidad_publicacion, v.created_at, v.created_at, v.deleted_at
FROM (VALUES
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer',            'publico',    NOW() - INTERVAL '10 days', NULL::timestamp),
    ('ana_gomez',   'Nueva sesión de fotos en el estudio, ¡pronto la comparto!',       'seguidores', NOW() - INTERVAL '8 days',  NULL::timestamp),
    ('carlos_ruiz', 'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', 'publico', NOW() - INTERVAL '7 days', NULL::timestamp),
    ('laura_diaz',  'Explorando nuevas paletas de color para el rediseño #diseno',    'privado',    NOW() - INTERVAL '6 days',  NULL::timestamp),
    ('juan_perez',  'Buen partido el de anoche, aunque perdimos jaja',                 'publico',    NOW() - INTERVAL '5 days',  NULL::timestamp),
    ('maria_lopez', 'Repasando para el parcial de bases de datos #universidad',       'publico',    NOW() - INTERVAL '4 days',  NULL::timestamp),
    ('sofia_martin','5 tips de marketing digital para emprendedores #marketing',      'publico',    NOW() - INTERVAL '3 days',  NULL::timestamp),
    ('ana_gomez',   'Publicación eliminada de prueba (soft delete)',                  'publico',    NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days')
) AS v(autor_username, contenido, privacidad, created_at, deleted_at)
JOIN usuarios u ON u.username = v.autor_username;

-- ------------------------------------------------------------
-- 5. medios (adjuntos a publicaciones)
-- ------------------------------------------------------------
INSERT INTO medios (publicacion_id, tipo, url_original, url_thumbnail, formato, tamano_bytes, orden, created_at)
SELECT p.id, v.tipo::tipo_medio, v.url_original, v.url_thumbnail, v.formato, v.tamano_bytes, v.orden, p.created_at
FROM (VALUES
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer',       'imagen', 'https://cdn.example.com/media/atardecer_full.jpg', 'https://cdn.example.com/media/atardecer_thumb.jpg', 'jpg', 2048576, 0),
    ('carlos_ruiz', 'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', 'imagen', 'https://cdn.example.com/media/query_plan.png', 'https://cdn.example.com/media/query_plan_thumb.png', 'png', 512340, 0),
    ('sofia_martin','5 tips de marketing digital para emprendedores #marketing', 'video', 'https://cdn.example.com/media/tips_marketing.mp4', 'https://cdn.example.com/media/tips_marketing_thumb.jpg', 'mp4', 15728640, 0),
    ('sofia_martin','5 tips de marketing digital para emprendedores #marketing', 'imagen', 'https://cdn.example.com/media/tips_slide2.jpg', 'https://cdn.example.com/media/tips_slide2_thumb.jpg', 'jpg', 734500, 1)
) AS v(autor_username, contenido, tipo, url_original, url_thumbnail, formato, tamano_bytes, orden)
JOIN usuarios u ON u.username = v.autor_username
JOIN publicaciones p ON p.autor_id = u.id AND p.contenido = v.contenido;

-- ------------------------------------------------------------
-- 6. etiquetas
-- ------------------------------------------------------------
INSERT INTO etiquetas (nombre) VALUES
    ('cali'),
    ('atardecer'),
    ('postgresql'),
    ('diseno'),
    ('universidad'),
    ('marketing')
ON CONFLICT (nombre) DO NOTHING;

-- ------------------------------------------------------------
-- 7. publicacion_etiquetas
-- ------------------------------------------------------------
INSERT INTO publicacion_etiquetas (publicacion_id, etiqueta_id)
SELECT p.id, e.id
FROM (VALUES
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'cali'),
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'atardecer'),
    ('carlos_ruiz', 'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', 'postgresql'),
    ('laura_diaz',  'Explorando nuevas paletas de color para el rediseño #diseno', 'diseno'),
    ('maria_lopez', 'Repasando para el parcial de bases de datos #universidad', 'universidad'),
    ('sofia_martin','5 tips de marketing digital para emprendedores #marketing', 'marketing')
) AS v(autor_username, contenido, etiqueta_nombre)
JOIN usuarios u ON u.username = v.autor_username
JOIN publicaciones p ON p.autor_id = u.id AND p.contenido = v.contenido
JOIN etiquetas e ON e.nombre = v.etiqueta_nombre
ON CONFLICT (publicacion_id, etiqueta_id) DO NOTHING;

-- ------------------------------------------------------------
-- 8. reacciones (like / dislike)
-- ------------------------------------------------------------
INSERT INTO reacciones (publicacion_id, usuario_id, tipo, created_at)
SELECT p.id, u.id, v.tipo::tipo_reaccion, NOW() - INTERVAL '2 days'
FROM (VALUES
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'carlos_ruiz',  'like'),
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'laura_diaz',   'like'),
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'juan_perez',   'dislike'),
    ('carlos_ruiz', 'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', 'ana_gomez', 'like'),
    ('sofia_martin','5 tips de marketing digital para emprendedores #marketing', 'maria_lopez', 'like')
) AS v(autor_username, contenido, reactor_username, tipo)
JOIN usuarios u ON u.username = v.reactor_username
JOIN usuarios autor ON autor.username = v.autor_username
JOIN publicaciones p ON p.autor_id = autor.id AND p.contenido = v.contenido
ON CONFLICT (publicacion_id, usuario_id) DO NOTHING;

-- ------------------------------------------------------------
-- 9. comentarios (nivel 1: sin padre)
-- ------------------------------------------------------------
INSERT INTO comentarios (publicacion_id, usuario_id, comentario_padre_id, contenido, created_at, updated_at, deleted_at)
SELECT p.id, u.id, NULL, v.contenido_comentario, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', NULL
FROM (VALUES
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'carlos_ruiz', '¡Qué foto tan increíble!'),
    ('ana_gomez',   'Atardecer increíble en el río Cali #cali #atardecer', 'laura_diaz',  '¿Con qué cámara la tomaste?'),
    ('carlos_ruiz', 'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', 'ana_gomez', 'Muy útil, gracias por compartir')
) AS v(autor_username, contenido, comentarista_username, contenido_comentario)
JOIN usuarios u ON u.username = v.comentarista_username
JOIN usuarios autor ON autor.username = v.autor_username
JOIN publicaciones p ON p.autor_id = autor.id AND p.contenido = v.contenido;

-- ------------------------------------------------------------
-- 9b. comentarios (nivel 2: respuestas anidadas)
-- ------------------------------------------------------------
INSERT INTO comentarios (publicacion_id, usuario_id, comentario_padre_id, contenido, created_at, updated_at, deleted_at)
SELECT padre.publicacion_id, u.id, padre.id, v.respuesta, NOW() - INTERVAL '20 hours', NOW() - INTERVAL '20 hours', NULL
FROM (VALUES
    ('¿Con qué cámara la tomaste?', 'ana_gomez', 'La tomé con una Sony A7 III, ¡gracias por preguntar!')
) AS v(contenido_padre, respondedor_username, respuesta)
JOIN comentarios padre ON padre.contenido = v.contenido_padre AND padre.comentario_padre_id IS NULL
JOIN usuarios u ON u.username = v.respondedor_username;

-- ------------------------------------------------------------
-- 10. notificaciones
-- ------------------------------------------------------------
INSERT INTO notificaciones (usuario_id, tipo, origen_usuario_id, publicacion_id, comentario_id, leida, created_at)
SELECT
    dest.id,
    v.tipo::tipo_notificacion,
    origen.id,
    p.id,
    c.id,
    v.leida,
    NOW() - INTERVAL '1 day'
FROM (VALUES
    ('ana_gomez',   'carlos_ruiz', 'nuevo_seguidor',          NULL::text, NULL::text, false),
    ('ana_gomez',   'laura_diaz',  'solicitud_seguimiento',   NULL::text, NULL::text, false),
    ('ana_gomez',   'carlos_ruiz', 'like',                    'Atardecer increíble en el río Cali #cali #atardecer', NULL::text, true),
    ('ana_gomez',   'carlos_ruiz', 'comentario',               'Atardecer increíble en el río Cali #cali #atardecer', '¡Qué foto tan increíble!', false),
    ('ana_gomez',   'ana_gomez',   'respuesta_comentario',    'Atardecer increíble en el río Cali #cali #atardecer', 'La tomé con una Sony A7 III, ¡gracias por preguntar!', false),
    ('carlos_ruiz', 'ana_gomez',   'like',                    'Optimizando queries en PostgreSQL, tremendo el uso de índices GIN #postgresql', NULL::text, false)
) AS v(dest_username, origen_username, tipo, contenido_pub, contenido_comentario, leida)
JOIN usuarios dest ON dest.username = v.dest_username
JOIN usuarios origen ON origen.username = v.origen_username
LEFT JOIN publicaciones p ON p.contenido = v.contenido_pub
LEFT JOIN comentarios c ON c.contenido = v.contenido_comentario;

-- ------------------------------------------------------------
-- 11. bloqueos
-- ------------------------------------------------------------
INSERT INTO bloqueos (usuario_id, usuario_bloqueado_id, created_at)
SELECT u.id, b.id, NOW() - INTERVAL '4 days'
FROM (VALUES
    ('ana_gomez', 'spam_bot99'),
    ('laura_diaz','spam_bot99')
) AS v(usuario_username, bloqueado_username)
JOIN usuarios u ON u.username = v.usuario_username
JOIN usuarios b ON b.username = v.bloqueado_username
ON CONFLICT (usuario_id, usuario_bloqueado_id) DO NOTHING;

-- ------------------------------------------------------------
-- 12. denuncias
-- ------------------------------------------------------------
INSERT INTO denuncias (usuario_reportante_id, usuario_reportado_id, publicacion_id, comentario_id, motivo, estado, created_at)
SELECT
    reportante.id,
    reportado.id,
    p.id,
    c.id,
    v.motivo,
    v.estado::estado_denuncia,
    NOW() - INTERVAL '3 days'
FROM (VALUES
    ('ana_gomez',  'spam_bot99', NULL::text, NULL::text, 'Cuenta de spam con enlaces sospechosos', 'pendiente'),
    ('carlos_ruiz','spam_bot99', NULL::text, NULL::text, 'Comentarios repetitivos de publicidad', 'revisada'),
    ('laura_diaz', NULL,        'Publicación eliminada de prueba (soft delete)', NULL::text, 'Contenido inapropiado', 'descartada')
) AS v(reportante_username, reportado_username, contenido_pub, contenido_comentario, motivo, estado)
JOIN usuarios reportante ON reportante.username = v.reportante_username
LEFT JOIN usuarios reportado ON reportado.username = v.reportado_username
LEFT JOIN publicaciones p ON p.contenido = v.contenido_pub
LEFT JOIN comentarios c ON c.contenido = v.contenido_comentario;

-- ------------------------------------------------------------
-- 13. actividad_historial
-- ------------------------------------------------------------
INSERT INTO actividad_historial (usuario_id, tipo_actividad, descripcion, created_at)
SELECT u.id, v.tipo_actividad, v.descripcion, NOW() - INTERVAL '1 day'
FROM (VALUES
    ('ana_gomez',   'LOGIN',              'Inicio de sesión desde nueva ubicación'),
    ('ana_gomez',   'PUBLICACION_CREADA', 'Creó una nueva publicación'),
    ('carlos_ruiz', 'PERFIL_ACTUALIZADO', 'Actualizó su biografía'),
    ('laura_diaz',  'LOGIN',              'Inicio de sesión'),
    ('maria_lopez', 'COMENTARIO_CREADO',  'Comentó en una publicación'),
    ('spam_bot99',  'CUENTA_REPORTADA',   'La cuenta fue reportada por otro usuario')
) AS v(username, tipo_actividad, descripcion)
JOIN usuarios u ON u.username = v.username;

COMMIT;
