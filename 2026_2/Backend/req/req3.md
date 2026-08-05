<!-- ...existing code... -->

# Requisitos funcionales (RF)

- RF-01 — Registro e inicio de sesión  
  - Permitir registro con email/usuario/contraseña y autenticación (JWT).  
  - Criterio: usuario obtiene token válido al iniciar sesión.

- RF-02 — Perfil de usuario  
  - Crear/editar perfil: nombre, bio, avatar, ubicación, enlace, visibilidad (público/privado).  
  - Criterio: cambios reflejados en vista pública/privada.

- RF-03 — Relaciones (seguir/amistad)  
  - Seguir/unfollow; en perfiles privados, solicitudes de seguimiento y aprobación.  
  - Criterio: estado de relación afecta visibilidad y feed.

- RF-04 — Crear publicación  
  - Crear post con texto y/o imágenes (1..N), metadatos (fecha, autor, privacidad, etiquetas).  
  - Criterio: post visible según privacidad y relaciones.

- RF-05 — Editar y eliminar publicación  
  - Autor puede editar o eliminar su publicación.  
  - Criterio: cambios aplicados o contenido removido de feeds.

- RF-06 — Feed / muro  
  - Mostrar feed personalizado (seguidores, sugerencias); paginación/infinite scroll; orden por relevancia/fecha.  
  - Criterio: feed devuelve páginas correctas y orden esperado.

- RF-07 — Reacciones (like / dislike)  
  - Usuarios pueden dar like o dislike (una reacción por usuario; cambiar o quitar).  
  - Criterio: contador y estado por usuario correctos.

- RF-08 — Comentarios  
  - Crear, editar y eliminar comentarios; soporte de respuestas anidadas (1 nivel).  
  - Criterio: comentarios aparecen correctamente y autores pueden modificarlos/eliminarlos.

- RF-09 — Notificaciones  
  - Notificaciones para nuevos seguidores, likes, dislikes, comentarios, respuestas y solicitudes de seguimiento.  
  - Criterio: usuario recibe notificaciones y puede marcarlas como leídas.

- RF-10 — Búsqueda y exploración  
  - Buscar usuarios, hashtags y publicaciones; sección Explorar con contenido trending.  
  - Criterio: resultados paginados y filtrables.

- RF-11 — Privacidad y bloqueo  
  - Bloquear/denunciar usuarios; gestionar visibilidad de perfil/publicaciones.  
  - Criterio: acciones aplican inmediatamente; denuncias registradas.

- RF-12 — Gestión de medios  
  - Subida, optimización (thumbnails), almacenamiento y eliminación de imágenes; límites de tamaño/formato.  
  - Criterio: imágenes servidas en resoluciones múltiples y errores manejados.

- RF-13 — Historial y actividad  
  - Registro de actividad básica (último login, acciones relevantes) visible al usuario.  
  - Criterio: historial consultable por el usuario.

<!-- ...existing code... -->