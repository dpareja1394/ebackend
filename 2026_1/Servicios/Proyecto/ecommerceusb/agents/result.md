# Respuestas (Rol: Docente USB — Backend)

## 1. ¿Qué podríamos mejorar en el flujo que hace el update de usuario?

En el flujo actual (`PUT /user/{id}` → `UserServiceImpl.updateUser`) hay varias mejoras claras a nivel de diseño, validación y performance:

1. **Mover validaciones a Bean Validation (Jakarta Validation) y simplificar el service**
   - Hoy el `updateUser` tiene muchas validaciones manuales (`null`, `isBlank`, `<= 0`). Eso “infla” el método y lo hace repetitivo (también aparece en `createUser`).
   - Recomendación: anotar el DTO `UpdateUserRequest` con validaciones como `@NotBlank`, `@NotNull`, `@Positive`, `@Email`, etc., y activar `@Valid` en el controller:
     - En `UserController.updateUser(...)`: `public ResponseEntity<UserResponse> updateUser(..., @Valid @RequestBody UpdateUserRequest req)`.
   - Ventaja: el service queda enfocado en reglas de negocio (unicidad, integridad referencial, etc.) y el error sale con códigos HTTP adecuados (400).

2. **Manejo de errores con excepciones de dominio y respuestas HTTP coherentes**
   - Actualmente se lanza `Exception` genérica. Eso dificulta distinguir entre:
     - Validación (400),
     - No encontrado (404),
     - Conflicto de unicidad (409).
   - Recomendación: crear excepciones específicas (por ejemplo `BadRequestException`, `NotFoundException`, `ConflictException`) y un `@RestControllerAdvice` que las traduzca a respuestas HTTP consistentes.

3. **Evitar parsear fechas como `String` en el service**
   - Hoy `birthDate` llega como `String` y se parsea en el service con `LocalDate.parse(..., DateTimeFormatter.ofPattern("yyyy-MM-dd"))`.
   - Recomendación didáctica (y más limpia): en el DTO usar `LocalDate birthDate` y, si necesitan formato fijo, usar `@JsonFormat(pattern = "yyyy-MM-dd")`.
   - Ventaja: menos lógica técnica en el service y menos chance de errores de formato.

4. **Reducir queries innecesarias en el update (optimización real)**
   - En el método actual se hacen consultas de unicidad siempre:
     - `existsByIdNotAndEmail(...)`
     - `existsByIdNotAndDocumentNumberAndDocumentTypeId(...)`
   - Mejora: **primero cargar el usuario** y solo si el estudiante cambió email/documento ejecutar las validaciones:
     - Si `req.getEmail().equals(user.getEmail())`, no consulten `existsBy...Email`.
     - Si `req.getDocumentNumber().equals(user.getDocumentNumber())` y el `documentTypeId` no cambia, no consulten `existsBy...Document`.
   - Esto baja el número de queries cuando el update no toca campos “únicos”.

5. **Diferenciar PUT (reemplazo) vs PATCH (parcial)**
   - El método actual exige todos los campos (comportamiento de “reemplazo total”), lo cual es coherente con `PUT`.
   - Si el requerimiento real es “actualizar solo lo que cambie”, entonces lo correcto es exponer un `PATCH /user/{id}` con un DTO parcial (campos opcionales) y reglas claras.
   - Recomendación: definir explícitamente en el diseño de la API cuál comportamiento quieren.

6. **`@Transactional` en operaciones de escritura**
   - Para `updateUser` (y `createUser`) es buena práctica usar `@Transactional` en el service: asegura consistencia si en el futuro el update toca otras entidades o hay más pasos.

7. **Consolidar reglas duplicadas entre create y update**
   - Las validaciones de campos y las reglas de unicidad son casi las mismas en `createUser` y `updateUser`.
   - Recomendación: extraer un validador/reutilizar un método privado o componente de validación para no duplicar lógica.

## 2. ¿Cómo podríamos optimizar los métodos que se encuentran en la implementación de UserServiceImpl?

Aquí optimización significa: menos código repetido, menos queries, mejor separación de responsabilidades y mayor mantenibilidad.

1. **Inyección por constructor en vez de `@Autowired` en campos**
   - Cambiar a inyección por constructor (idealmente con `final`): hace la clase más testeable y evita estados parcialmente inicializados.

2. **`getUsers()` puede simplificarse**
   - Hoy se consulta `findAll()`, se revisa `isEmpty()` y luego se mapea.
   - Se puede retornar directamente el mapeo; si la lista es vacía, el mapeo naturalmente retorna vacío (o con streams).

3. **Evitar repetir validaciones manuales en cada método**
   - `getUserById`, `getUserByEmail`, `createUser`, `updateUser` hacen validaciones similares.
   - Bean Validation en DTOs + validación simple en path/query params reduce bastante el ruido.

4. **Optimizar `updateUser` evitando consultas de unicidad cuando no aplica**
   - Como les decía arriba: cargar el `User` primero y validar unicidad solo si cambian los campos que tienen restricciones de unicidad.
   - Esto es una optimización tangible, no solo estética.

5. **Centralizar el formateador de fechas (si siguen usando `String`)**
   - Si por ahora mantienen `birthDate` como `String`, eviten crear `DateTimeFormatter.ofPattern("yyyy-MM-dd")` dentro del método.
   - Definan un `private static final DateTimeFormatter BIRTHDATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;` (o el patrón que estén usando) y reutilícenlo.

6. **Dejar la unicidad también “blindada” a nivel base de datos**
   - Aunque el service valida con `existsBy...`, en producción siempre deben existir constraints/índices únicos:
     - `email` único,
     - (`document_type_id`, `document_number`) único (si esa es la regla del dominio).
   - Razón: evita condiciones de carrera (dos requests simultáneas que “pasan” la validación y luego chocan).

7. **Excepciones más específicas y mensajes consistentes**
   - Además de mejorar la API, mejora el testing: ya no dependen de `Exception` genérica, sino de tipos claros.

Si quieren, como ejercicio de clase, pueden refactorizar `updateUser` en estos pasos: (1) cargar usuario, (2) validar cambios, (3) validar unicidad solo si aplica, (4) mapear cambios, (5) guardar, (6) mapear response. Esa estructura queda más alineada con una capa `Service` limpia.
