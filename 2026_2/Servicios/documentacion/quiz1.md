# Quiz 1 — Fundamentos de Microservicios con Spring Boot

**Basado en:** microservicio `autosusbcali` (paquete `co.edu.usbcali.autosusbcali`)
**Tipo:** Selección múltiple con única respuesta
**Nivel:** Introductorio (conceptos básicos vistos en clase)

> Instrucciones: selecciona la opción correcta (a, b, c o d) para cada pregunta. Las respuestas correctas están al final del documento.

---

### 1. ¿Qué anotación se usa para marcar la clase principal de una aplicación Spring Boot?

a) `@RestController`
b) `@SpringBootApplication`
c) `@Entity`
d) `@Repository`

### 2. En `AutosusbcaliApplication.java`, ¿qué método arranca la aplicación?

a) `SpringApplication.run(...)`
b) `main.start(...)`
c) `Application.boot(...)`
d) `Spring.init(...)`

### 3. ¿Qué anotación convierte una clase Java en una entidad de base de datos (JPA)?

a) `@Table`
b) `@Column`
c) `@Entity`
d) `@Id`

### 4. En la clase `Rol`, ¿qué anotación indica cuál atributo es la llave primaria?

a) `@GeneratedValue`
b) `@Id`
c) `@Column`
d) `@PrimaryKey`

### 5. ¿Para qué sirve `@GeneratedValue(strategy = GenerationType.IDENTITY)` en el campo `id` de `Rol`?

a) Para validar que el id no sea nulo
b) Para que la base de datos genere automáticamente el valor del id
c) Para convertir el id en texto
d) Para ordenar los registros por id

### 6. La anotación `@Table(name = "roles")` en la clase `Rol` indica que...

a) La clase se llama `roles`
b) La entidad se mapea a la tabla `roles` en la base de datos
c) Se debe crear un DTO llamado `roles`
d) El controlador expone la ruta `/roles`

### 7. En Lombok, ¿qué hace la anotación `@Data` usada en `Rol.java`?

a) Solo genera el constructor vacío
b) Genera automáticamente getters, setters, `equals`, `hashCode` y `toString`
c) Convierte la clase en un controlador REST
d) Crea la conexión a la base de datos

### 8. ¿Qué diferencia hay entre `@NoArgsConstructor` y `@AllArgsConstructor` de Lombok?

a) Son exactamente lo mismo
b) `@NoArgsConstructor` genera un constructor sin parámetros y `@AllArgsConstructor` genera uno con todos los atributos
c) `@NoArgsConstructor` es solo para DTOs
d) `@AllArgsConstructor` elimina los getters

### 9. En `Rol.java`, la anotación `@Column(name = "nombre", length = 50, nullable = false, unique = true)` indica que el campo `nombre`...

a) Puede repetirse en varios registros
b) Es opcional
c) No puede ser nulo y no puede repetirse (debe ser único)
d) Solo acepta números

### 10. ¿Qué interfaz extiende `RolRepository` para obtener operaciones básicas de base de datos (guardar, buscar, eliminar) sin escribir SQL?

a) `RolService`
b) `JpaRepository`
c) `RolController`
d) `EntityManager`

### 11. En `RolRepository`, el método `findByActivo(Boolean activo)` es un ejemplo de...

a) Una consulta SQL escrita a mano
b) Un método derivado (Spring Data genera la consulta a partir del nombre del método)
c) Un mapper
d) Un DTO

### 12. ¿Qué anotación marca la clase `RolController` como un controlador que expone servicios REST (respuestas en JSON)?

a) `@Controller`
b) `@Service`
c) `@RestController`
d) `@Component`

### 13. La anotación `@RequestMapping("/api/roles")` en `RolController` sirve para...

a) Definir el nombre de la base de datos
b) Definir la ruta base común para todos los endpoints de ese controlador
c) Crear una tabla llamada `api_roles`
d) Inyectar el repositorio

### 14. ¿Qué anotación se usa en `RolController` para exponer un endpoint que responde a peticiones HTTP GET?

a) `@PostMapping`
b) `@GetMapping`
c) `@PutMapping`
d) `@DeleteMapping`

### 15. En el método `obtenerPorId`, la anotación `@PathVariable Long id` se usa para...

a) Leer el `id` que viene en el cuerpo (body) de la petición
b) Leer el `id` que viene como parte de la URL, por ejemplo `/api/roles/por-id/5`
c) Leer el `id` de un archivo de configuración
d) Generar un nuevo `id`

### 16. ¿Qué es un DTO (Data Transfer Object) como `ObtenerRolResponse`?

a) Una entidad que se guarda directamente en la base de datos
b) Un objeto simple usado para transportar datos entre capas (por ejemplo, del backend al cliente) sin exponer toda la entidad
c) Una clase que reemplaza al controlador
d) Una anotación de Spring

### 17. `ObtenerRolResponse` está definido como un `record` de Java. ¿Cuál es una ventaja de usar `record` para un DTO?

a) Permite heredar de varias clases al mismo tiempo
b) Genera automáticamente una forma corta e inmutable de declarar los atributos, constructor y getters
c) Convierte la clase en una entidad JPA
d) Elimina la necesidad de tener controladores

### 18. ¿Cuál es el propósito principal de una clase *Mapper* como `RolMapper`?

a) Ejecutar consultas SQL directamente
b) Transformar/convertir objetos de un tipo a otro, por ejemplo de `Rol` (entidad) a `ObtenerRolResponse` (DTO)
c) Reemplazar al repositorio
d) Validar la conexión a la base de datos

### 19. ¿Por qué es una buena práctica devolver un DTO (`ObtenerRolResponse`) en vez de devolver directamente la entidad `Rol` en algunos endpoints?

a) Porque las entidades no se pueden convertir a JSON
b) Porque permite controlar exactamente qué información se expone al cliente, sin depender directamente de la estructura interna de la base de datos
c) Porque los DTOs son más rápidos de compilar
d) Porque Spring Boot lo exige obligatoriamente

### 20. La relación `@ManyToMany` entre `Usuario` y `Rol` (a través de la tabla `usuarios_roles`) representa qué tipo de relación de base de datos?

a) Uno a uno
b) Uno a muchos
c) Muchos a muchos
d) Ninguna relación

---

## Respuestas

1. b
2. a
3. c
4. b
5. b
6. b
7. b
8. b
9. c
10. b
11. b
12. c
13. b
14. b
15. b
16. b
17. b
18. b
19. b
20. c
