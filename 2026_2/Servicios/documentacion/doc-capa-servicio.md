# Capa de Servicios — guía para el microservicio `autosusbcali`

## 1. Propósito de este documento

Hasta ahora, `RolController` habla directamente con `RolRepository`:

```java
@GetMapping
public List<ObtenerRolResponse> obtenerTodos() {
    return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
}
```

Esto funciona para consultas simples, pero mezcla dos responsabilidades distintas en la misma clase: **recibir peticiones HTTP** y **decidir qué hacer con los datos**. A medida que aparezcan `crear`, `actualizar` y `eliminar`, esa mezcla se vuelve difícil de mantener (validaciones, reglas de negocio, transacciones, etc. quedarían en el controlador).

Este documento explica:

- para qué sirve la capa de **Servicio** (`service`)
- cómo se crea, con el ejemplo concreto de `RolService`
- cómo se usan **DTOs Request** para crear, actualizar y eliminar
- cómo quedan los métodos `GET` que hoy están en el controlador

## 2. ¿Para qué sirve la capa de Servicio?

El **Service** es la capa donde vive la **lógica de negocio**. Es el intermediario entre el controlador (que solo habla HTTP) y el repositorio (que solo habla base de datos).

```text
Cliente HTTP
    │
    ▼
Controller   → solo recibe la petición, delega al Service y devuelve una respuesta HTTP
    │
    ▼
Service      → reglas de negocio, validaciones, orquesta Repository + Mapper
    │
    ▼
Repository   → solo consulta o modifica la base de datos
```

Con esta separación:

- El **Controller** queda simple: recibe el request, llama al service, arma el `ResponseEntity`.
- El **Service** concentra las reglas: por ejemplo, "no se puede crear un rol con un nombre que ya existe" o "un rol no se elimina si tiene usuarios asociados".
- El **Repository** sigue siendo solo acceso a datos, sin lógica de negocio.
- El **Mapper** sigue siendo solo conversión entre `domain` y `dto`.

## 3. ¿Quién llama al Mapper: el Controller o el Service?

A partir de ahora, **el Mapper se usa dentro del Service**, no en el Controller. El Controller ya no debe conocer `RolMapper`.

- El **Service** recibe/devuelve DTOs (`Request` de entrada, `Response` de salida) y por dentro convierte hacia/desde la entidad (`Rol`) usando el `Mapper`.
- El **Controller** solo trabaja con DTOs, nunca con la entidad `Rol` directamente.

## 4. Cómo se crea un Service

Para mantener el mismo estilo que ya usa el proyecto (inyección de dependencias con Lombok, igual que en `RolController`), un Service se crea así:

1. Se crea el paquete `service` (mismo nivel que `controller`, `repository`, `mapper`, `dto`, `domain`).
2. Se crea una clase anotada con `@Service`.
3. Se inyecta el `Repository` (y cualquier otro service/repository que necesite) como atributo `final`, usando `@AllArgsConstructor` de Lombok, igual que en `RolController`.
4. Cada método público del Service representa **una operación de negocio** (obtener, crear, actualizar, eliminar), no un endpoint HTTP.

```java
package co.edu.usbcali.autosusbcali.service;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RolService {
    private final RolRepository rolRepository;
    // más adelante: RolMapper si se usa como bean, o sus métodos estáticos
}
```

> Nota: `RolMapper` hoy es una clase de métodos `static`, así que se puede seguir llamando como `RolMapper.rolAObtenerRolResponse(...)` sin necesidad de inyectarlo.

## 5. DTOs Request: `crear`, `actualizar` y `eliminar`

Igual que existe `dto/response/ObtenerRolResponse.java` para las respuestas, se crea un paquete `dto/request` para lo que entra en `crear`, `actualizar` y `eliminar`. Se recomienda un `record` por operación, porque son inmutables y muy cortos de escribir (igual que ya se hizo con `ObtenerRolResponse`).

### 5.1 `CrearRolRequest`

```java
package co.edu.usbcali.autosusbcali.dto.request;

public record CrearRolRequest(String nombre, String descripcion, Boolean activo) {
}
```

No trae `id`: el `id` lo genera la base de datos (`@GeneratedValue`).

### 5.2 `ActualizarRolRequest`

```java
package co.edu.usbcali.autosusbcali.dto.request;

public record ActualizarRolRequest(String nombre, String descripcion, Boolean activo) {
}
```

El `id` del rol a actualizar llega por la URL (`@PathVariable`), igual que ya hace `obtenerPorId`. El body solo trae los datos que se van a modificar.

### 5.3 `EliminarRolRequest`

Para mantener el mismo patrón en las tres operaciones de escritura, se usa un DTO también para eliminar:

```java
package co.edu.usbcali.autosusbcali.dto.request;

public record EliminarRolRequest(Long id) {
}
```

> En la mayoría de los proyectos Spring, `eliminar` solo necesita el `id` de la URL y no lleva un `body`. Aquí se documenta con DTO porque así se decidió estandarizar las tres operaciones del curso; si más adelante se necesita guardar un motivo de eliminación o datos de auditoría, este DTO ya queda listo para crecer sin tener que cambiar la firma del método.

## 6. `RolService` completo (con los GET actuales incluidos)

Los métodos `GET` que hoy están en `RolController` (`obtenerTodos`, `obtenerTodosActivos`, `obtenerTodosInactivos`, `obtenerPorNombre`, `obtenerPorId`) se mueven al Service. El Service ya devuelve DTOs listos para que el Controller los entregue tal cual.

```java
package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.ActualizarRolRequest;
import co.edu.usbcali.autosusbcali.dto.request.CrearRolRequest;
import co.edu.usbcali.autosusbcali.dto.request.EliminarRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.mapper.RolMapper;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    // ----- GET: antes estaban en el Controller -----

    public List<ObtenerRolResponse> obtenerTodos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
    }

    public List<ObtenerRolResponse> obtenerTodosActivos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findByActivo(true));
    }

    public List<ObtenerRolResponse> obtenerTodosInactivos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findByActivo(false));
    }

    public Optional<ObtenerRolResponse> obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .map(RolMapper::rolAObtenerRolResponse);
    }

    public Optional<ObtenerRolResponse> obtenerPorId(Long id) {
        return rolRepository.findById(id)
                .map(RolMapper::rolAObtenerRolResponse);
    }

    // ----- CREATE -----

    @Transactional
    public ObtenerRolResponse crear(CrearRolRequest request) {
        Rol rol = new Rol();
        rol.setNombre(request.nombre());
        rol.setDescripcion(request.descripcion());
        rol.setActivo(request.activo());

        Rol rolGuardado = rolRepository.save(rol);
        return RolMapper.rolAObtenerRolResponse(rolGuardado);
    }

    // ----- UPDATE -----

    @Transactional
    public Optional<ObtenerRolResponse> actualizar(Long id, ActualizarRolRequest request) {
        return rolRepository.findById(id)
                .map(rol -> {
                    rol.setNombre(request.nombre());
                    rol.setDescripcion(request.descripcion());
                    rol.setActivo(request.activo());
                    return RolMapper.rolAObtenerRolResponse(rolRepository.save(rol));
                });
    }

    // ----- DELETE -----

    @Transactional
    public boolean eliminar(EliminarRolRequest request) {
        if (!rolRepository.existsById(request.id())) {
            return false;
        }
        rolRepository.deleteById(request.id());
        return true;
    }
}
```

Puntos a resaltar para el curso:

- `obtenerPorNombre` y `obtenerPorId` devuelven `Optional<ObtenerRolResponse>`: el Service **no decide** el código HTTP (eso es trabajo del Controller), solo dice si encontró o no el dato.
- `crear`, `actualizar` y `eliminar` van con `@Transactional`: si algo falla a mitad de camino, la operación se revierte completa.
- El `Mapper` solo se usa aquí, dentro del Service.

## 7. Cómo queda el Controller después del cambio

El Controller deja de hablar con `RolRepository` y con `RolMapper`, y pasa a hablar solo con `RolService`:

```java
package co.edu.usbcali.autosusbcali.controller;

import co.edu.usbcali.autosusbcali.dto.request.ActualizarRolRequest;
import co.edu.usbcali.autosusbcali.dto.request.CrearRolRequest;
import co.edu.usbcali.autosusbcali.dto.request.EliminarRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.service.RolService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public List<ObtenerRolResponse> obtenerTodos() {
        return rolService.obtenerTodos();
    }

    @GetMapping("/activos")
    public List<ObtenerRolResponse> obtenerTodosActivos() {
        return rolService.obtenerTodosActivos();
    }

    @GetMapping("/inactivos")
    public List<ObtenerRolResponse> obtenerTodosInactivos() {
        return rolService.obtenerTodosInactivos();
    }

    @GetMapping("/por-nombre/{nombre}")
    public ResponseEntity<ObtenerRolResponse> obtenerPorNombre(@PathVariable String nombre) {
        return rolService.obtenerPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/por-id/{id}")
    public ResponseEntity<ObtenerRolResponse> obtenerPorId(@PathVariable Long id) {
        return rolService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ObtenerRolResponse> crear(@RequestBody CrearRolRequest request) {
        return new ResponseEntity<>(rolService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObtenerRolResponse> actualizar(@PathVariable Long id,
                                                          @RequestBody ActualizarRolRequest request) {
        return rolService.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = rolService.eliminar(new EliminarRolRequest(id));
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
```

Nota sobre `eliminar`: el `id` sigue llegando por la URL (`@PathVariable`), como es estándar en REST; el Controller simplemente lo empaqueta en `EliminarRolRequest` antes de pasarlo al Service, para respetar el mismo patrón de DTO en las tres operaciones de escritura.

## 8. Estructura de paquetes resultante

```text
co/edu/usbcali/autosusbcali/
├── controller/
│   └── RolController.java        (solo HTTP, delega todo al Service)
├── service/
│   └── RolService.java           (lógica de negocio, usa Repository + Mapper)
├── mapper/
│   └── RolMapper.java            (domain ↔ dto, se usa desde el Service)
├── repository/
│   └── RolRepository.java        (acceso a datos, sin cambios)
├── domain/
│   └── Rol.java                  (entidad JPA, sin cambios)
└── dto/
    ├── request/
    │   ├── CrearRolRequest.java
    │   ├── ActualizarRolRequest.java
    │   └── EliminarRolRequest.java
    └── response/
        └── ObtenerRolResponse.java
```

## 9. Checklist para crear el Service de cualquier otra entidad

Cuando se avance con otras entidades (`Usuario`, `Vehiculo`, etc.), repetir esta misma receta:

- [ ] Crear `XxxService` en el paquete `service`, anotada con `@Service` y `@AllArgsConstructor`.
- [ ] Inyectar el `Repository` correspondiente como atributo `final`.
- [ ] Mover a el Service los métodos `GET` que hoy estén en el Controller.
- [ ] Crear `CrearXxxRequest`, `ActualizarXxxRequest` y `EliminarXxxRequest` en `dto/request`.
- [ ] Los métodos de escritura (`crear`, `actualizar`, `eliminar`) van con `@Transactional`.
- [ ] El `Mapper` se llama solo desde el Service, nunca desde el Controller.
- [ ] El Controller queda delgado: recibe DTOs, llama al Service, arma el `ResponseEntity`.

## 10. Próximos pasos sugeridos

1. Crear `RolService` tal como se documentó aquí y ajustar `RolController` para que use el Service.
2. Repetir el mismo patrón con las demás entidades a medida que se les agreguen Controller y Mapper.
3. Evaluar si conviene una excepción de negocio propia (por ejemplo `RolNoEncontradoException`) en vez de `Optional`, cuando se quiera centralizar el manejo de errores con `@ControllerAdvice`.

---

Esta guía complementa a [doc-tecnica-servicios.md](./doc-tecnica-servicios.md) y a [doc-onboarding-equipo-servicios.md](./doc-onboarding-equipo-servicios.md), y toma como base el microservicio [autosusbcali](../microservices/autosusbcali/).
