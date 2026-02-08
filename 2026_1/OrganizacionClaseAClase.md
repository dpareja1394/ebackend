# Cómo enseñar “capa por capa” durante 16 semanas (aplicable a cualquiera)

### Capas recomendadas (y qué enseñas en cada una)

1. **API (Controller/Handlers)**
    
    - REST, validación de request, códigos HTTP, DTOs
        
2. **Application / Use Cases (Service)**
    
    - Orquestación de casos de uso (transacciones, reglas)
        
3. **Domain (Entidades + reglas)**
    
    - Modelos, invariantes, estados, políticas
        
4. **Persistence (Repository/DAO)**
    
    - SQL, consultas, índices, migraciones
        
5. **Infrastructure**
    
    - Config, logging, métricas, jobs, integración (simulada)
        
6. **Cross-cutting**
    
    - auth, errores estándar, tracing, testing
        

### Un plan sugerido por semanas (resumen)

- **Sem 1–2:** setup, Docker Compose (API + Postgres), migraciones, convenciones, estructura por capas
    
- **Sem 3–4:** CRUD base + validaciones + errores estándar + paginación/filtros
    
- **Sem 5–7:** reglas de negocio core (solapamientos / stock / agenda / vencimientos) con transacciones
    
- **Sem 8–9:** auth (JWT), roles (admin/user), seguridad básica
    
- **Sem 10–11:** reporting (consultas SQL más reales), optimización con índices, explain
    
- **Sem 12–13:** testing (unit + integración), datos semilla, contratos API
    
- **Sem 14:** jobs/background (especialmente útil en Vehículos; opcional en otros)
    
- **Sem 15:** observabilidad (logs estructurados, métricas simples), hardening
    
- **Sem 16:** cierre: refactor, documentación OpenAPI, demo final
    

---

# Cómo meter Go específicamente para algunos GET (sin dolor)

La forma más limpia (y muy enseñable) es separar **Read API** y **Write API**:

## Opción recomendada: **CQRS ligero**

- **Java (Spring Boot)**: comandos / escritura (POST/PUT/PATCH/DELETE) + reglas transaccionales “pesadas”
    
- **Go (Gin/Fiber/net/http)**: consultas **GET** optimizadas, paginación, filtros, endpoints de lectura
    

### Qué GET “tiene sentido” delegar a Go (por proyecto)

- **Canchas**
    
    - `GET /courts/availability?date=&venue_id=`
        
    - `GET /reservations?date_from=&date_to=&court_id=`
        
- **Turnos médicos**
    
    - `GET /doctors/{id}/availability?date=`
        
    - `GET /appointments?doctor_id=&date=`
        
- **E-commerce**
    
    - `GET /products?search=&category=&min=&max=`
        
    - `GET /orders/{id}` (solo lectura, con join a items)
        
- **Vehículos**
    
    - `GET /vehicles/{id}/timeline`
        
    - `GET /notifications?status=pending` (o dashboard)
        

### Patrón de integración (simple para clase)

- Ambos servicios apuntan a **la misma Postgres**
    
- Separas esquemas o prefijos de tablas si quieres orden (opcional)
    
- En Go, endpoints read-only con queries SQL claras e índices
    
- En Java, toda escritura con transacciones y validaciones
    

> Esto además te permite enseñar **contratos**: OpenAPI compartido y pruebas de contrato simples.

---

## Recomendación para tomar la decisión mañana

Si quieres una decisión “inteligente” con estudiantes, elige según el enfoque del curso:

- Si quieres **equilibrio + reglas interesantes sin explotar** → **Reservas de canchas**
    
- Si quieres **mucha lógica de negocio y agenda** → **Turnos médicos**
    
- Si quieres **proyecto tipo industria y retador** → **Mini e-commerce**
    
- Si quieres **jobs + fechas + notificaciones + enfoque servicios** → **Vehículos**
    

---

Si me dices cuál de los 4 te está ganando (aunque sea por intuición), te dejo listo para mañana:

- **lista priorizada de requisitos (Must/Should/Could)**,
    
- **primer backlog de historias de usuario**, y
    
- una propuesta exacta de **qué endpoints quedan en Go** vs Java (con criterios).