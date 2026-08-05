# Requisitos Funcionales (RF)
Sistema — Red Social (Perfiles, Publicaciones, Interacciones y Notificaciones)

## Resumen y alcance
- Objetivo: Proveer una red social donde los usuarios puedan registrarse, construir un perfil, seguir a otros usuarios, publicar contenido (texto/imágenes), interactuar mediante reacciones y comentarios, y mantenerse informados mediante notificaciones y búsqueda/exploración de contenido.
- Alcance incluido: autenticación, perfiles, relaciones (seguir/amistad), publicaciones, feed, reacciones, comentarios, notificaciones, búsqueda/exploración, privacidad/bloqueo, gestión de medios e historial de actividad.
- Actores: Usuario (autenticado), Usuario visitante (no autenticado, acceso limitado), Sistema (procesos de feed/notificaciones).

---

## 1. Autenticación
- **RF-01 — Registro e inicio de sesión**
  - Permitir registro con email/usuario/contraseña y autenticación (JWT).
  - Criterio de aceptación: el usuario obtiene un token válido al iniciar sesión.

## 2. Perfil de usuario
- **RF-02 — Perfil de usuario**
  - Crear/editar perfil: nombre, bio, avatar, ubicación, enlace, visibilidad (público/privado).
  - Criterio de aceptación: los cambios se reflejan en la vista pública/privada.

## 3. Relaciones entre usuarios
- **RF-03 — Relaciones (seguir/amistad)**
  - Seguir/dejar de seguir; en perfiles privados, solicitudes de seguimiento y aprobación.
  - Criterio de aceptación: el estado de la relación afecta la visibilidad y el feed.

## 4. Publicaciones
- **RF-04 — Crear publicación**
  - Crear post con texto y/o imágenes (1..N), con metadatos (fecha, autor, privacidad, etiquetas).
  - Criterio de aceptación: el post es visible según su privacidad y las relaciones del autor.
- **RF-05 — Editar y eliminar publicación**
  - El autor puede editar o eliminar su publicación.
  - Criterio de aceptación: los cambios se aplican o el contenido se elimina de los feeds.
- **RF-06 — Feed / muro**
  - Mostrar feed personalizado (seguidores, sugerencias); paginación/infinite scroll; orden por relevancia/fecha.
  - Criterio de aceptación: el feed devuelve páginas correctas en el orden esperado.

## 5. Interacciones
- **RF-07 — Reacciones (like / dislike)**
  - Los usuarios pueden dar like o dislike (una reacción por usuario; puede cambiarse o quitarse).
  - Criterio de aceptación: el contador y el estado por usuario son correctos.
- **RF-08 — Comentarios**
  - Crear, editar y eliminar comentarios; soporte de respuestas anidadas (1 nivel).
  - Criterio de aceptación: los comentarios aparecen correctamente y los autores pueden modificarlos/eliminarlos.

## 6. Notificaciones
- **RF-09 — Notificaciones**
  - Notificaciones para nuevos seguidores, likes, dislikes, comentarios, respuestas y solicitudes de seguimiento.
  - Criterio de aceptación: el usuario recibe notificaciones y puede marcarlas como leídas.

## 7. Búsqueda y exploración
- **RF-10 — Búsqueda y exploración**
  - Buscar usuarios, hashtags y publicaciones; sección "Explorar" con contenido trending.
  - Criterio de aceptación: los resultados son paginados y filtrables.

## 8. Privacidad y moderación
- **RF-11 — Privacidad y bloqueo**
  - Bloquear/denunciar usuarios; gestionar visibilidad de perfil/publicaciones.
  - Criterio de aceptación: las acciones aplican inmediatamente y las denuncias quedan registradas.

## 9. Gestión de medios
- **RF-12 — Gestión de medios**
  - Subida, optimización (thumbnails), almacenamiento y eliminación de imágenes; límites de tamaño/formato.
  - Criterio de aceptación: las imágenes se sirven en resoluciones múltiples y los errores se manejan correctamente.

## 10. Historial y actividad
- **RF-13 — Historial y actividad**
  - Registro de actividad básica (último login, acciones relevantes) visible al usuario.
  - Criterio de aceptación: el historial es consultable por el usuario propietario.

---

## Modelo de datos lógico (referencia)
- **Usuario**: id, email, username, password_hash, last_login, created_at
- **Perfil**: id, usuario_id, nombre, bio, avatar_url, ubicacion, enlace, visibilidad
- **Relacion** (seguir/amistad): id, seguidor_id, seguido_id, estado (pendiente/aceptada/rechazada)
- **Publicacion**: id, autor_id, contenido, privacidad, created_at, updated_at, deleted_at
- **Medio**: id, publicacion_id, url_original, url_thumbnail, tipo, formato, tamano_bytes, orden
- **Etiqueta** (hashtag): id, nombre
- **PublicacionEtiqueta**: publicacion_id, etiqueta_id
- **Reaccion**: id, publicacion_id, usuario_id, tipo (like/dislike), created_at
- **Comentario**: id, publicacion_id, usuario_id, comentario_padre_id, contenido, created_at, updated_at, deleted_at
- **Notificacion**: id, usuario_id, tipo, origen_usuario_id, publicacion_id, comentario_id, leida, created_at
- **Bloqueo**: id, usuario_id, usuario_bloqueado_id, created_at
- **Denuncia**: id, usuario_reportante_id, usuario_reportado_id, publicacion_id, comentario_id, motivo, estado, created_at
- **ActividadHistorial**: id, usuario_id, tipo_actividad, descripcion, created_at