# Borrador de Entidades — Sistema de Gestión Compra-Venta de Vehículos (PostgreSQL)

## Módulos y entidades identificadas

| Módulo | Entidades |
|---|---|
| GU — Usuarios y Accesos | `usuarios`, `roles`, `permisos`, `roles_permisos`, `usuarios_roles`, `tokens_recuperacion` |
| GV — Inventario de Vehículos | `marcas`, `modelos`, `vehiculos`, `fichas_tecnicas`, `imagenes_vehiculo`, `documentos_vehiculo` |
| BF — Búsqueda y Filtros | `busquedas_guardadas` |
| CF — Cotización y Financiamiento | `cotizaciones`, `accesorios_cotizacion`, `entidades_financieras`, `solicitudes_credito` |
| PV — Proceso de Venta | `oportunidades_venta`, `reservas`, `negociaciones`, `ventas` |
| DL — Documentación Legal | `contratos`, `tramites_traspaso`, `verificaciones_runt` |
| PG — Pagos | `pagos`, `transacciones_pasarela`, `facturas` |
| PS — Postventa | `garantias`, `citas_servicio`, `pqrs` |
| NT — Notificaciones | `notificaciones` |
| RA — Reportes y Administración | `auditoria`, `parametros_sistema` |

---

## GU — Gestión de Usuarios y Accesos

### `usuarios`
Tabla unificada para usuarios internos (asesor, gerente, admin) y compradores, diferenciados por `tipo`.

| Columna | Tipo | Restricciones | Notas |
|---|---|---|---|
| `id` | BIGSERIAL | PK | |
| `nombres` | VARCHAR(100) | NOT NULL | |
| `apellidos` | VARCHAR(100) | NOT NULL | |
| `email` | VARCHAR(150) | UNIQUE, NOT NULL | |
| `celular` | VARCHAR(20) | | |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt |
| `tipo` | VARCHAR(10) | NOT NULL, CHECK IN ('INTERNO','COMPRADOR') | |
| `tipo_documento` | VARCHAR(10) | NOT NULL | CC, NIT, CE… |
| `numero_documento` | VARCHAR(30) | UNIQUE, NOT NULL | |
| `email_verificado` | BOOLEAN | DEFAULT FALSE | RF-GU-03 |
| `celular_verificado` | BOOLEAN | DEFAULT FALSE | RF-GU-03 |
| `activo` | BOOLEAN | DEFAULT TRUE | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() | |
| `fecha_actualizacion` | TIMESTAMPTZ | | ON UPDATE |

**Índices:** `idx_usuarios_email`, `idx_usuarios_numero_documento`, `idx_usuarios_tipo`

---

### `roles`
Catálogo de roles del sistema.

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `nombre` | VARCHAR(50) | UNIQUE, NOT NULL |
| `descripcion` | TEXT | |
| `activo` | BOOLEAN | DEFAULT TRUE |

> Valores esperados: `ADMIN`, `ASESOR`, `GERENTE_VENTAS`, `COMPRADOR`

---

### `permisos`
Catálogo de permisos por módulo y acción.

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `modulo` | VARCHAR(50) | NOT NULL |
| `accion` | VARCHAR(30) | NOT NULL, CHECK IN ('LECTURA','ESCRITURA','APROBACION') |
| `descripcion` | TEXT | |

**Constraint:** `UNIQUE(modulo, accion)`

---

### `roles_permisos`
Relación muchos a muchos entre roles y permisos.

| Columna | Tipo | Restricciones |
|---|---|---|
| `rol_id` | BIGINT | PK, FK → `roles.id` |
| `permiso_id` | BIGINT | PK, FK → `permisos.id` |

---

### `usuarios_roles`
Asignación de roles a usuarios.

| Columna | Tipo | Restricciones |
|---|---|---|
| `usuario_id` | BIGINT | PK, FK → `usuarios.id` |
| `rol_id` | BIGINT | PK, FK → `roles.id` |

---

### `tokens_recuperacion`
Tokens de un solo uso para restablecimiento de contraseña (RF-GU-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `usuario_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `token` | VARCHAR(255) | UNIQUE, NOT NULL |
| `usado` | BOOLEAN | DEFAULT FALSE |
| `fecha_expiracion` | TIMESTAMPTZ | NOT NULL |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_tokens_token`, `idx_tokens_usuario_id`

---

## GV — Gestión de Inventario de Vehículos

### `marcas`
Catálogo de marcas de vehículos.

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `nombre` | VARCHAR(100) | UNIQUE, NOT NULL |
| `activo` | BOOLEAN | DEFAULT TRUE |

---

### `modelos`
Catálogo de modelos por marca.

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `marca_id` | BIGINT | NOT NULL, FK → `marcas.id` |
| `nombre` | VARCHAR(100) | NOT NULL |

**Constraint:** `UNIQUE(marca_id, nombre)`
**Índice:** `idx_modelos_marca_id`

---

### `vehiculos`
Entidad central del inventario (RF-GV-01 a RF-GV-05).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vin` | VARCHAR(17) | UNIQUE, NOT NULL |
| `placa` | VARCHAR(10) | UNIQUE | Para usados |
| `modelo_id` | BIGINT | NOT NULL, FK → `modelos.id` |
| `anio` | SMALLINT | NOT NULL |
| `tipo` | VARCHAR(6) | NOT NULL, CHECK IN ('NUEVO','USADO') |
| `color` | VARCHAR(50) | |
| `kilometraje` | INTEGER | DEFAULT 0, CHECK >= 0 |
| `precio_base` | NUMERIC(15,2) | NOT NULL, CHECK > 0 |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('INGRESADO','EN_VERIFICACION','VERIFICADO','PUBLICADO','RESERVADO','EN_VENTA','VENDIDO','RETIRADO') |
| `descripcion` | TEXT | |
| `publicado_en` | TIMESTAMPTZ | | Fecha de publicación en catálogo |
| `retirado_en` | TIMESTAMPTZ | | RF-GV-05 |
| `usuario_registra_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |
| `fecha_actualizacion` | TIMESTAMPTZ | |

**Índices:** `idx_vehiculos_vin`, `idx_vehiculos_placa`, `idx_vehiculos_estado`, `idx_vehiculos_modelo_id`, `idx_vehiculos_anio`, `idx_vehiculos_precio_base`

---

### `fichas_tecnicas`
Hoja técnica detallada por vehículo (RF-GV-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | UNIQUE, NOT NULL, FK → `vehiculos.id` |
| `motor` | VARCHAR(100) | |
| `transmision` | VARCHAR(50) | |
| `combustible` | VARCHAR(30) | |
| `cilindraje_cc` | INTEGER | |
| `potencia_hp` | INTEGER | |
| `num_puertas` | SMALLINT | |
| `capacidad_pasajeros` | SMALLINT | |
| `num_airbags` | SMALLINT | |
| `url_documento_pdf` | VARCHAR(500) | | PDF de ficha técnica |

---

### `imagenes_vehiculo`
Galería de imágenes por vehículo (RF-GV-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `url` | VARCHAR(500) | NOT NULL |
| `orden` | SMALLINT | DEFAULT 0 |
| `es_principal` | BOOLEAN | DEFAULT FALSE |
| `fecha_carga` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_imagenes_vehiculo_id`

---

### `documentos_vehiculo`
Documentos legales del vehículo (SOAT, tecnomecánica, etc.) (RF-DL-01).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `tipo_documento` | VARCHAR(50) | NOT NULL, CHECK IN ('SOAT','TECNOMECANICA','TARJETA_PROPIEDAD','RTM','OTRO') |
| `url` | VARCHAR(500) | NOT NULL |
| `fecha_expedicion` | DATE | |
| `fecha_vencimiento` | DATE | |
| `validado` | BOOLEAN | DEFAULT FALSE |
| `fecha_validacion` | TIMESTAMPTZ | |
| `usuario_valida_id` | BIGINT | FK → `usuarios.id` |

**Índice:** `idx_docs_vehiculo_id`

---

## BF — Búsqueda y Filtros

### `busquedas_guardadas`
Búsquedas y alertas guardadas por el comprador (RF-BF-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `usuario_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `nombre` | VARCHAR(100) | |
| `criterios` | JSONB | NOT NULL | Filtros combinables: marca, precio, año… |
| `alerta_activa` | BOOLEAN | DEFAULT FALSE |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_busquedas_usuario_id`, `idx_busquedas_criterios` (GIN sobre JSONB)

---

## CF — Cotización y Financiamiento

### `cotizaciones`
Cotización formal generada para un comprador (RF-CF-01).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `codigo` | VARCHAR(30) | UNIQUE, NOT NULL |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `asesor_id` | BIGINT | FK → `usuarios.id` |
| `precio_base` | NUMERIC(15,2) | NOT NULL |
| `descuento` | NUMERIC(15,2) | DEFAULT 0 |
| `impuestos` | NUMERIC(15,2) | DEFAULT 0 |
| `precio_total` | NUMERIC(15,2) | NOT NULL |
| `vigencia_hasta` | DATE | NOT NULL |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('BORRADOR','ENVIADA','ACEPTADA','RECHAZADA','VENCIDA') |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índices:** `idx_cotizaciones_vehiculo_id`, `idx_cotizaciones_comprador_id`

---

### `accesorios_cotizacion`
Accesorios incluidos en una cotización.

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `cotizacion_id` | BIGINT | NOT NULL, FK → `cotizaciones.id` |
| `descripcion` | VARCHAR(200) | NOT NULL |
| `precio` | NUMERIC(12,2) | NOT NULL, CHECK >= 0 |

**Índice:** `idx_accesorios_cotizacion_id`

---

### `entidades_financieras`
Catálogo de entidades financieras aliadas (RF-CF-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `nombre` | VARCHAR(100) | NOT NULL |
| `nit` | VARCHAR(20) | UNIQUE, NOT NULL |
| `endpoint_api` | VARCHAR(500) | | URL de integración |
| `activo` | BOOLEAN | DEFAULT TRUE |

---

### `solicitudes_credito`
Expediente de crédito enviado a entidad financiera (RF-CF-03, RF-CF-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `cotizacion_id` | BIGINT | NOT NULL, FK → `cotizaciones.id` |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `entidad_financiera_id` | BIGINT | NOT NULL, FK → `entidades_financieras.id` |
| `monto_solicitado` | NUMERIC(15,2) | NOT NULL |
| `cuota_inicial` | NUMERIC(15,2) | |
| `plazo_meses` | SMALLINT | NOT NULL, CHECK > 0 |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('ENVIADA','EN_ESTUDIO','APROBADA','RECHAZADA') |
| `observaciones` | TEXT | |
| `fecha_envio` | TIMESTAMPTZ | DEFAULT NOW() |
| `fecha_respuesta` | TIMESTAMPTZ | |

**Índices:** `idx_solicitudes_comprador_id`, `idx_solicitudes_cotizacion_id`

---

## PV — Proceso de Venta

### `oportunidades_venta`
Registro de cada oportunidad comercial (RF-PV-02).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `asesor_id` | BIGINT | FK → `usuarios.id` |
| `estado` | VARCHAR(30) | NOT NULL, CHECK IN ('NUEVA','EN_NEGOCIACION','COTIZACION_ENVIADA','CREDITO_EN_TRAMITE','GANADA','PERDIDA') |
| `origen` | VARCHAR(20) | CHECK IN ('WEB','PRESENCIAL','TELEFONO') |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |
| `fecha_actualizacion` | TIMESTAMPTZ | |

**Índices:** `idx_oportunidades_vehiculo_id`, `idx_oportunidades_asesor_id`, `idx_oportunidades_comprador_id`

---

### `reservas`
Apartado de un vehículo con vigencia limitada (RF-PV-01).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | UNIQUE, NOT NULL, FK → `vehiculos.id` | Solo una reserva activa por vehículo |
| `oportunidad_id` | BIGINT | NOT NULL, FK → `oportunidades_venta.id` |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `asesor_id` | BIGINT | FK → `usuarios.id` |
| `monto_reserva` | NUMERIC(12,2) | |
| `vigencia_hasta` | TIMESTAMPTZ | NOT NULL |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('ACTIVA','EXPIRADA','CANCELADA','CONVERTIDA') |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

---

### `negociaciones`
Registro de descuentos y negociaciones por oportunidad (RF-PV-03, RF-PV-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `oportunidad_id` | BIGINT | NOT NULL, FK → `oportunidades_venta.id` |
| `asesor_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `precio_ofertado` | NUMERIC(15,2) | NOT NULL |
| `descuento_porcentaje` | NUMERIC(5,2) | CHECK BETWEEN 0 AND 100 |
| `descuento_valor` | NUMERIC(15,2) | |
| `requiere_aprobacion` | BOOLEAN | DEFAULT FALSE |
| `estado_aprobacion` | VARCHAR(20) | CHECK IN ('PENDIENTE','APROBADA','RECHAZADA') |
| `aprobador_id` | BIGINT | FK → `usuarios.id` | Gerente que aprueba |
| `fecha_aprobacion` | TIMESTAMPTZ | |
| `observaciones` | TEXT | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_negociaciones_oportunidad_id`

---

### `ventas`
Registro del cierre formal de venta (RF-PV-05).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `numero_venta` | VARCHAR(30) | UNIQUE, NOT NULL |
| `oportunidad_id` | BIGINT | UNIQUE, NOT NULL, FK → `oportunidades_venta.id` |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `asesor_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `precio_final` | NUMERIC(15,2) | NOT NULL |
| `forma_pago` | VARCHAR(10) | NOT NULL, CHECK IN ('CONTADO','CREDITO') |
| `solicitud_credito_id` | BIGINT | FK → `solicitudes_credito.id` | Nulo si es contado |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('EN_PROCESO','CERRADA','ANULADA') |
| `fecha_cierre` | TIMESTAMPTZ | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índices:** `idx_ventas_vehiculo_id`, `idx_ventas_comprador_id`, `idx_ventas_asesor_id`

---

## DL — Documentación Legal y Trámites

### `verificaciones_runt`
Consultas al RUNT para vehículos usados (RF-DL-02).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `placa` | VARCHAR(10) | NOT NULL |
| `tiene_prendas` | BOOLEAN | |
| `tiene_embargos` | BOOLEAN | |
| `reporte_hurto` | BOOLEAN | |
| `estado_legal` | VARCHAR(50) | |
| `respuesta_raw` | JSONB | | Respuesta completa del RUNT |
| `usuario_consulta_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `fecha_consulta` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_runt_vehiculo_id`

---

### `contratos`
Contrato de compraventa generado automáticamente (RF-DL-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `venta_id` | BIGINT | UNIQUE, NOT NULL, FK → `ventas.id` |
| `numero_contrato` | VARCHAR(50) | UNIQUE, NOT NULL |
| `url_documento` | VARCHAR(500) | |
| `firmado_comprador` | BOOLEAN | DEFAULT FALSE |
| `firmado_asesor` | BOOLEAN | DEFAULT FALSE |
| `fecha_firma` | TIMESTAMPTZ | |
| `fecha_generacion` | TIMESTAMPTZ | DEFAULT NOW() |

---

### `tramites_traspaso`
Seguimiento del trámite de traspaso ante tránsito (RF-DL-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `venta_id` | BIGINT | NOT NULL, FK → `ventas.id` |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('INICIADO','EN_TRAMITE','EN_TRANSITO','COMPLETADO','RECHAZADO') |
| `observaciones` | TEXT | |
| `usuario_gestiona_id` | BIGINT | FK → `usuarios.id` |
| `fecha_inicio` | TIMESTAMPTZ | DEFAULT NOW() |
| `fecha_completado` | TIMESTAMPTZ | |

---

## PG — Pagos

### `pagos`
Registro de pagos asociados a una venta (RF-PG-01, RF-PG-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `venta_id` | BIGINT | NOT NULL, FK → `ventas.id` |
| `tipo_pago` | VARCHAR(30) | NOT NULL, CHECK IN ('CUOTA_INICIAL','SALDO','DESEMBOLSO_CREDITO') |
| `monto` | NUMERIC(15,2) | NOT NULL, CHECK > 0 |
| `medio_pago` | VARCHAR(30) | NOT NULL, CHECK IN ('TRANSFERENCIA','TARJETA','PSE','EFECTIVO','DESEMBOLSO') |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('PENDIENTE','PROCESANDO','APROBADO','RECHAZADO') |
| `referencia_externa` | VARCHAR(100) | | Referencia del banco/pasarela |
| `fecha_pago` | TIMESTAMPTZ | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_pagos_venta_id`

---

### `transacciones_pasarela`
Detalle de transacciones procesadas por pasarela (RF-PG-02).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `pago_id` | BIGINT | NOT NULL, FK → `pagos.id` |
| `proveedor` | VARCHAR(50) | NOT NULL | PayU, Wompi, etc. |
| `referencia_pasarela` | VARCHAR(100) | UNIQUE, NOT NULL |
| `estado_pasarela` | VARCHAR(50) | |
| `monto` | NUMERIC(15,2) | |
| `respuesta_raw` | JSONB | |
| `fecha_transaccion` | TIMESTAMPTZ | DEFAULT NOW() |

---

### `facturas`
Factura electrónica conforme a la DIAN (RF-PG-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `venta_id` | BIGINT | UNIQUE, NOT NULL, FK → `ventas.id` |
| `numero_factura` | VARCHAR(50) | UNIQUE, NOT NULL |
| `cufe` | VARCHAR(255) | UNIQUE | Código único DIAN |
| `url_documento` | VARCHAR(500) | |
| `subtotal` | NUMERIC(15,2) | NOT NULL |
| `iva` | NUMERIC(15,2) | NOT NULL |
| `total` | NUMERIC(15,2) | NOT NULL |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('GENERADA','ENVIADA_DIAN','ACEPTADA','RECHAZADA') |
| `fecha_emision` | TIMESTAMPTZ | DEFAULT NOW() |

---

## PS — Postventa

### `garantias`
Garantía registrada al cerrar venta (RF-PS-01).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `venta_id` | BIGINT | UNIQUE, NOT NULL, FK → `ventas.id` |
| `tipo` | VARCHAR(20) | NOT NULL, CHECK IN ('FABRICA','CONCESIONARIA') |
| `descripcion` | TEXT | |
| `duracion_meses` | SMALLINT | NOT NULL, CHECK > 0 |
| `fecha_inicio` | DATE | NOT NULL |
| `fecha_fin` | DATE | NOT NULL |
| `condiciones` | TEXT | |

---

### `citas_servicio`
Agendamiento de citas de mantenimiento postventa (RF-PS-02).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `vehiculo_id` | BIGINT | NOT NULL, FK → `vehiculos.id` |
| `asesor_id` | BIGINT | FK → `usuarios.id` |
| `tipo_servicio` | VARCHAR(100) | NOT NULL |
| `fecha_cita` | TIMESTAMPTZ | NOT NULL |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('AGENDADA','CONFIRMADA','REALIZADA','CANCELADA') |
| `observaciones` | TEXT | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índice:** `idx_citas_comprador_id`, `idx_citas_fecha_cita`

---

### `pqrs`
Radicación y seguimiento de PQRS (RF-PS-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `numero_radicado` | VARCHAR(30) | UNIQUE, NOT NULL |
| `comprador_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `tipo` | VARCHAR(20) | NOT NULL, CHECK IN ('PETICION','QUEJA','RECLAMO','SUGERENCIA') |
| `asunto` | VARCHAR(200) | NOT NULL |
| `descripcion` | TEXT | NOT NULL |
| `venta_id` | BIGINT | FK → `ventas.id` | Opcional |
| `estado` | VARCHAR(20) | NOT NULL, CHECK IN ('RADICADA','EN_GESTION','RESUELTA','CERRADA') |
| `usuario_asignado_id` | BIGINT | FK → `usuarios.id` |
| `respuesta` | TEXT | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |
| `fecha_cierre` | TIMESTAMPTZ | |

**Índice:** `idx_pqrs_comprador_id`, `idx_pqrs_estado`

---

## NT — Notificaciones

### `notificaciones`
Notificaciones enviadas a los usuarios por canal (RF-NT-01, RF-NT-02).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `usuario_id` | BIGINT | NOT NULL, FK → `usuarios.id` |
| `tipo` | VARCHAR(50) | NOT NULL | RESERVA, CREDITO, PAGO, VENTA, CITA, ALERTA_BUSQUEDA |
| `titulo` | VARCHAR(200) | NOT NULL |
| `cuerpo` | TEXT | |
| `canal` | VARCHAR(10) | NOT NULL, CHECK IN ('EMAIL','SMS','PUSH','IN_APP') |
| `leida` | BOOLEAN | DEFAULT FALSE |
| `referencia_tipo` | VARCHAR(50) | | Entidad relacionada (venta, reserva…) |
| `referencia_id` | BIGINT | | ID de la entidad relacionada |
| `fecha_envio` | TIMESTAMPTZ | |
| `fecha_creacion` | TIMESTAMPTZ | DEFAULT NOW() |

**Índices:** `idx_notificaciones_usuario_id`, `idx_notificaciones_leida`

---

## RA — Reportes y Administración

### `auditoria`
Log inmutable de acciones críticas (RF-RA-04).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `usuario_id` | BIGINT | FK → `usuarios.id` | Nullable para acciones del sistema |
| `accion` | VARCHAR(100) | NOT NULL |
| `entidad` | VARCHAR(50) | NOT NULL |
| `entidad_id` | BIGINT | |
| `datos_anteriores` | JSONB | |
| `datos_nuevos` | JSONB | |
| `ip_origen` | VARCHAR(45) | |
| `user_agent` | TEXT | |
| `fecha` | TIMESTAMPTZ | DEFAULT NOW() |

> **Nota:** Esta tabla no debe tener UPDATE ni DELETE en producción (append-only).

**Índices:** `idx_auditoria_usuario_id`, `idx_auditoria_entidad`, `idx_auditoria_fecha`

---

### `parametros_sistema`
Parámetros configurables del negocio (RF-RA-03).

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `clave` | VARCHAR(100) | UNIQUE, NOT NULL |
| `valor` | TEXT | NOT NULL |
| `tipo_dato` | VARCHAR(10) | CHECK IN ('STRING','NUMBER','BOOLEAN','JSON') |
| `descripcion` | TEXT | |
| `usuario_actualiza_id` | BIGINT | FK → `usuarios.id` |
| `fecha_actualizacion` | TIMESTAMPTZ | |

---

## Diagrama de dependencias clave (FK principales)

```
marcas ──< modelos ──< vehiculos >── imagenes_vehiculo
                          │         └── fichas_tecnicas
                          │         └── documentos_vehiculo
                          │         └── verificaciones_runt
                          │
usuarios >── oportunidades_venta ──< negociaciones
                  │
                  ├──< reservas
                  │
                  └──> ventas ──> contratos
                           │      └── tramites_traspaso
                           ├──> pagos ──> transacciones_pasarela
                           ├──> facturas
                           ├──> garantias
                           └──> pqrs

usuarios >── cotizaciones ──< accesorios_cotizacion
                 └──> solicitudes_credito ──> entidades_financieras

usuarios >── busquedas_guardadas
usuarios >── notificaciones
usuarios >── tokens_recuperacion
usuarios >─< usuarios_roles >─< roles >─< roles_permisos >─< permisos
usuarios >── citas_servicio
```

---

## Resumen de entidades: 28 tablas

| # | Tabla | Módulo |
|---|---|---|
| 1 | `usuarios` | GU |
| 2 | `roles` | GU |
| 3 | `permisos` | GU |
| 4 | `roles_permisos` | GU |
| 5 | `usuarios_roles` | GU |
| 6 | `tokens_recuperacion` | GU |
| 7 | `marcas` | GV |
| 8 | `modelos` | GV |
| 9 | `vehiculos` | GV |
| 10 | `fichas_tecnicas` | GV |
| 11 | `imagenes_vehiculo` | GV |
| 12 | `documentos_vehiculo` | GV |
| 13 | `busquedas_guardadas` | BF |
| 14 | `cotizaciones` | CF |
| 15 | `accesorios_cotizacion` | CF |
| 16 | `entidades_financieras` | CF |
| 17 | `solicitudes_credito` | CF |
| 18 | `oportunidades_venta` | PV |
| 19 | `reservas` | PV |
| 20 | `negociaciones` | PV |
| 21 | `ventas` | PV |
| 22 | `verificaciones_runt` | DL |
| 23 | `contratos` | DL |
| 24 | `tramites_traspaso` | DL |
| 25 | `pagos` | PG |
| 26 | `transacciones_pasarela` | PG |
| 27 | `facturas` | PG |
| 28 | `garantias` | PS |
| 29 | `citas_servicio` | PS |
| 30 | `pqrs` | PS |
| 31 | `notificaciones` | NT |
| 32 | `auditoria` | RA |
| 33 | `parametros_sistema` | RA |

---

## Puntos a evaluar / decisiones pendientes

1. **`usuarios` unificado vs. separado** — Se unificó compradores e internos en una sola tabla por simplicidad. Si los atributos divergen mucho en el futuro, considerar `personas` + tablas especializadas.
2. **Historial de estados de vehículo** — Actualmente el estado actual está en `vehiculos`. Si se necesita trazabilidad completa del ciclo de vida, agregar tabla `historial_estados_vehiculo`.
3. **`busquedas_guardadas.criterios` como JSONB** — Flexible para filtros combinables, pero requiere validación en capa de aplicación.
4. **`auditoria` append-only** — Se recomienda revocar `UPDATE`/`DELETE` al rol de aplicación en producción.
5. **Índices adicionales** — Los índices sobre `fecha_creacion` y campos de estado pueden ser candidatos dependiendo del volumen de datos y consultas del dashboard.
6. **`reservas.vehiculo_id UNIQUE`** — Garantiza solo una reserva activa a nivel DB; la lógica de negocio debe validar también el `estado`.
