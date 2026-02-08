## Resumen comparativo rápido

|Proyecto|Qué enseña muy bien|Complejidad|Riesgos típicos|Ideal si el curso quiere…|
|---|---|--:|---|---|
|🏟️ Reservas de canchas|concurrencia, reglas de solapamiento, estados|Media|doble reserva, modelado de horarios|un backend “real” pero controlable|
|🩺 Turnos médicos|agenda recurrente, disponibilidad, reprogramaciones|Media–Alta|reglas de agenda se vuelven complejas|énfasis fuerte en lógica de negocio|
|🛒 Mini e-commerce|transacciones, stock, estados de orden, idempotencia|Alta|consistencia de inventario/pagos|un “capstone” tipo industria|
|🚗 Vehículos + notificaciones|jobs, vencimientos, reglas configurables, trazabilidad|Media|calendario de notificaciones, muchas reglas|backend orientado a “servicios” y automatización|

---

# 1) 🏟️ Sistema de reservas para canchas (backend)

### Requisitos funcionales (core)

1. **Gestión de sedes y canchas**
    
    - CRUD de sedes
        
    - CRUD de canchas (tipo, capacidad, horario base)
        
2. **Consulta de disponibilidad**
    
    - Ver disponibilidad por cancha y fecha
        
    - Filtros por sede, deporte, franja horaria
        
3. **Reservas**
    
    - Crear reserva con: cancha, fecha, hora inicio/fin, usuario
        
    - **Validación anti-solapamiento** (no permitir cruces)
        
    - Estados: `PENDING`, `CONFIRMED`, `CANCELLED`, `NO_SHOW` (mínimo 3)
        
4. **Cancelación / políticas**
    
    - Cancelar bajo reglas (ej. hasta X horas antes)
        
    - Motivo de cancelación (opcional)
        
5. **Administración**
    
    - Listado de reservas por cancha / sede / día
        
    - Bloqueos de cancha (mantenimiento/eventos)
        

### Extras “para subir nivel”

- Precios dinámicos por franja/día
    
- Pagos simulados (confirmación automática)
    
- Historial/auditoría de cambios
    

**Resumen:** es fácil de entender, y el reto técnico (solapamientos + transacciones) es excelente para backend.

---

# 2) 🩺 Sistema de turnos médicos (backend)

### Requisitos funcionales (core)

1. **Gestión de doctores y especialidades**
    
    - CRUD doctores
        
    - CRUD especialidades
        
    - Relación doctor ↔ especialidad
        
2. **Agenda y disponibilidad**
    
    - Configurar disponibilidad por doctor (bloques de atención)
        
    - Bloqueos: vacaciones/incapacidades/jornadas no disponibles
        
3. **Turnos**
    
    - Crear turno: paciente, doctor, fecha/hora, duración
        
    - **Evitar solapamiento** por doctor (y consultorio si aplica)
        
    - Estados: `SCHEDULED`, `CANCELLED`, `COMPLETED`, `NO_SHOW`
        
4. **Reprogramación y cancelación**
    
    - Reprogramar con reglas (p.ej. no el mismo día / con X horas)
        
5. **Vistas para usuarios**
    
    - Historial de turnos por paciente
        
    - Agenda diaria/semana por doctor
        

### Extras “para subir nivel”

- Generación automática de “slots” desde reglas recurrentes
    
- Lista de espera
    
- Check-in y flujo de atención
    

**Resumen:** lógica de negocio potente (más que canchas), pero puede crecer de forma desordenada si no se acota bien desde el inicio.

---

# 3) 🛒 Plataforma de pedidos (mini e-commerce backend)

### Requisitos funcionales (core)

1. **Catálogo**
    
    - CRUD productos y categorías
        
    - Búsqueda + filtros (texto, categoría, precio, disponibilidad)
        
2. **Carrito**
    
    - Crear/obtener carrito activo por usuario
        
    - Agregar/quitar items, cambiar cantidades
        
3. **Checkout / Orden**
    
    - Crear orden desde carrito (snapshot de precio)
        
    - Estados: `CREATED`, `PAID`, `CANCELLED` (+ opcionales)
        
4. **Inventario**
    
    - Descontar stock al pagar (o reservar al crear orden, según diseño)
        
    - Validar stock suficiente
        
5. **Pago simulado**
    
    - Endpoint para “pagar” una orden
        
    - **Idempotencia**: no duplicar pago ni doble descuento
        

### Extras “para subir nivel”

- Envíos y tracking
    
- Cupones/descuentos
    
- Reportes SQL (top productos, ventas por rango)
    

**Resumen:** muy “industria”, pero es la que más exige disciplina de diseño para no enredarse (stock/pagos/estados).

---

# 4) 🚗 Registro de vehículos + notificaciones (mantenimientos y documentos)

### Requisitos funcionales (core)

1. **Usuarios y vehículos**
    
    - CRUD vehículos (placa, tipo: auto/camioneta/moto/taxi, servicio)
        
2. **Documentos con vencimiento**
    
    - Registrar documentos por vehículo (tipo, fecha expedición, fecha vencimiento)
        
    - Estado: vigente / por vencer / vencido
        
3. **Mantenimientos**
    
    - Registrar mantenimientos (tipo, fecha, km, próxima fecha o km)
        
    - Planes base por tipo de vehículo (opcional)
        
4. **Notificaciones**
    
    - Configurar recordatorios (p.ej. 30/15/7 días antes)
        
    - Job diario/semanal que genera notificaciones pendientes
        
    - Registro de notificaciones enviadas (historial)
        
5. **Panel de próximos vencimientos**
    
    - “Timeline” por vehículo: próximos documentos y mantenimientos
        

### Extras “para subir nivel”

- Reglas especiales por tipo de vehículo (taxis con documentos adicionales)
    
- Importación CSV y validación masiva
    
- Plantillas por canal (email/sms simulado)
    

**Resumen:** muy backend (jobs, fechas, reglas), y perfecta para enseñar “servicios” sin necesidad de UI compleja.

