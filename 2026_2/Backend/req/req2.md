# Requisitos Funcionales y No Funcionales (RFS)
Sistema POS — Inventario, Facturación, Clientes, Usuarios y Roles
Fecha: 2026-08-05
Autor: Equipo de Producto

Resumen y alcance
- Objetivo: Proveer un sistema POS (Point of Sale) que permita realizar ventas en punto de venta, llevar la contabilidad de productos (entradas, salidas y rotación de inventario), gestionar clientes, emitir facturas y administrar usuarios y roles. Mantener todo dentro del bounded context POS / Inventario / Facturación / Usuarios.
- Alcance incluido:
  - Registro y gestión de productos.
  - Movimientos de inventario: entradas (compras, recepciones), salidas (ventas, devoluciones), ajustes y transferencias entre almacenes.
  - Cálculo y reporte de rotación de inventario y valoración de stock.
  - Gestión de clientes y facturación (incluye nota sobre facturación electrónica como opción).
  - Gestión de usuarios, roles y auditoría.
- Fuera de alcance:
  - Contabilidad general/ERP fuera del libro mayor expuesto por el inventario (se permite exportación de asientos), nómina, producción o gestión avanzada de proveedores más allá de registrar entradas.

Decisiones clave y supuestos
- Consistencia fuerte para transacciones que afectan stock y facturación.
- Método de valoración configurable: FIFO (por defecto), Promedio Ponderado, Costo Estándar.
- Cada movimiento genera un registro inmutable en el Stock Ledger (cardex).
- Soporte multi-almacén y multi-sucursal.
- Soporte multi-empresa lógico (tenant) con separación de datos por empresa.

Glosario
- SKU: identificador de producto.
- Stock Ledger / Cardex: registro cronológico e inmutable de movimientos de inventario.
- Turnover / Rotación: ratio de ventas respecto a inventario promedio en un periodo.

---

REQUISITOS FUNCIONALES (FR)
Cada FR incluye: Descripción, Actores, Prioridad y Criterios de Aceptación (CA).

FR-001 — Gestión de productos (CRUD)
- Descripción: Crear/leer/actualizar/eliminar productos con: SKU, nombre, descripción, unidad, categoría, costo, precio de venta, impuestos, control_inventario, atributos extensibles, códigos de barra.
- Actores: Administrador, Contador, Gestor de inventario.
- Prioridad: Alta
- CA:
  - Al crear un producto son obligatorios: SKU, nombre y unidad.
  - Producto puede desactivarse (soft delete).
  - Historial de cambios de precio y costo queda registrado.

FR-002 — Registro de almacenes
- Descripción: CRUD de almacenes/ubicaciones.
- Actores: Admin, Gestor de inventario.
- Prioridad: Alta
- CA:
  - Cada movimiento requiere almacén origen/destino.
  - Consulta de inventario por SKU + almacén disponible.

FR-003 — Movimientos de inventario (Entradas)
- Descripción: Registrar entradas por compra, ajuste positivo, devolución o stock inicial.
- Actores: Gestor de inventario, Contador.
- Prioridad: Alta
- CA:
  - Entrada con line-items (sku, qty, costo unitario, lote opt, referencia externa).
  - Genera StockLedger y actualiza StockBalance por almacén.
  - Registro inmutable con usuario y timestamp.

FR-004 — Movimientos de inventario (Salidas)
- Descripción: Registrar salidas por ventas, ajustes negativos o pérdidas.
- Actores: Cajero, Gestor de inventario.
- Prioridad: Alta
- CA:
  - Salida decrementa stock; crea StockLedger tipo=salida.
  - Si stock insuficiente, bloquear o permitir según flag "permitir_stock_negativo" por empresa.
  - Ventas deben vincularse a venta_id.

FR-005 — Ventas POS (checkout)
- Descripción: Proceso de venta: crear venta, lineas, impuestos, descuentos, cliente, pagos y comprobantes.
- Actores: Cajero, Administrador.
- Prioridad: Muy alta
- CA:
  - Venta crea salida de inventario atómica; rollback si falla.
  - Pagos soportan múltiples métodos (efectivo, tarjeta, crédito, mixto).
  - Genera ticket y factura (si aplica).

FR-006 — Facturación (invoices)
- Descripción: Emitir facturas vinculadas a ventas; soportar estados: draft, issued, canceled, refunded.
- Actores: Cajero, Contador.
- Prioridad: Muy alta
- CA:
  - Factura incluye número, fecha, cliente, líneas, impuestos y total.
  - Anulación/refund genera notas de crédito y ajusta inventario si corresponde.
  - Facturas emitidas son inmutables (solo notas/rectificaciones).

FR-007 — Gestión de clientes
- Descripción: CRUD de clientes con datos fiscales, contacto, límite de crédito, lista de precios preferencial e historial de ventas.
- Actores: Cajero, Contador, Ventas.
- Prioridad: Alta
- CA:
  - Búsqueda rápida por documento fiscal o nombre.
  - Límite de crédito bloquea ventas a crédito si se excede (configurable).

FR-008 — Usuarios y roles (RBAC)
- Descripción: CRUD usuarios y roles; asignación de permisos.
- Actores: SuperAdmin.
- Prioridad: Muy alta
- CA:
  - Permisos por acción; endpoints validan roles.
  - Auditoría de acciones críticas (user_id, timestamp, IP/terminal).
  - Autenticación por contraseña y sesiones de POS.

FR-009 — Control de caja / sesiones
- Descripción: Apertura/cierre de caja, arqueos y discrepancias.
- Actores: Cajero, Gerente.
- Prioridad: Alta
- CA:
  - No permitir ventas si caja no está abierta.
  - Cierre genera resumen por método de pago y registro de usuario.

FR-010 — Ajustes de inventario y autorizaciones
- Descripción: Ajustes manuales con motivo y usuario autorizador; histórico inmutable.
- Actores: Inventario, Gerente.
- Prioridad: Alta
- CA:
  - Ajuste crea StockLedger con motivo, referencia y usuario.
  - Doble confirmación si ajuste > X% (configurable).

FR-011 — Reporte: stock en mano y valoración
- Descripción: Mostrar stock actual por SKU/almacén y valor según método configurado.
- Actores: Contador, Gerente.
- Prioridad: Alta
- CA:
  - Filtros por fecha, almacén y categoría.
  - Valoración acorde a método (FIFO/avg/standard).

FR-012 — Reporte: rotación de inventario (turnover)
- Descripción: Métricas: ventas en unidades/valor, inventario promedio, días de inventario, turnover ratio.
- Actores: Gerente, Contador.
- Prioridad: Alta
- CA:
  - Selección de periodo; fórmulas documentadas y trazables.

FR-013 — Integridad transaccional
- Descripción: Operaciones que afectan inventario y facturación son atómicas.
- Actores: Sistema.
- Prioridad: Muy alta
- CA:
  - Venta: si cualquiera de inventario, pago o factura falla, todo revierte.

FR-014 — Exportes y asientos contables
- Descripción: Exportar movimientos y reportes en CSV/JSON para contabilidad externa.
- Actores: Contador.
- Prioridad: Media
- CA:
  - Exportes por rango de fechas con referencias a IDs.

FR-015 — Alertas de stock
- Descripción: Notificaciones cuando stock < reorder_point o discrepancias.
- Actores: Inventario, Gerente.
- Prioridad: Media
- CA:
  - Alerts por email o in-app listando SKUs por debajo del umbral.

FR-016 — Historial y trazabilidad
- Descripción: Mantener historial completo de cambios de precio, costo y cantidad.
- Actores: Contador, Auditor.
- Prioridad: Alta
- CA:
  - Consulta histórica por SKU con timeline.

FR-017 — Devoluciones y notas de crédito
- Descripción: Registrar devoluciones que ajusten inventario y emitan nota de crédito o reembolso.
- Actores: Cajero, Contador.
- Prioridad: Alta
- CA:
  - Devolución revierte salida original y genera nota de crédito vinculada.

FR-018 — Configuración por empresa
- Descripción: Parámetros por empresa: moneda, método de valoración, permitir_stock_negativo, impuestos, numeración de facturas.
- Actores: SuperAdmin.
- Prioridad: Alta
- CA:
  - Cambios auditados y aplican a cálculos futuros.

---
REQUISITOS NO FUNCIONALES (NFR)
- NFR-001 Disponibilidad: 99.5% durante horario comercial.
- NFR-002 Rendimiento: Ventas y actualizaciones < 500ms promedio; consultas < 2s para >1000 SKUs.
- NFR-003 Consistencia: ACID para cambios de stock/ventas.
- NFR-004 Seguridad: TLS, hash de contraseñas (bcrypt/argon2), control de sesiones y logs de auditoría.
- NFR-005 Backups: Snapshots diarios y backups incrementales.
- NFR-006 Escalabilidad: Multi-tenant lógico y separación de datos por empresa.
- NFR-007 Observabilidad: Logs estructurados y métricas.
- NFR-008 Retención: Configurable por legislación local.
- NFR-009 Internacionalización: Moneda, formato de fecha y número configurables.
- NFR-010 Usabilidad: UI POS optimizada para teclado y atajos.

---
MODELO DE DATOS LÓGICO (principalmente)
- Empresa: id, nombre, config_valoracion, moneda, impuestos, permitir_stock_negativo, created_at
- Usuario: id, nombre, email, password_hash, roles[], sucursal_id, activo, last_login
- Rol: id, nombre, permisos[]
- Producto: id, sku, nombre, descripcion, unidad, control_inventario (bool), costo_estandar, precio_venta, impuestos_aplicables, categoria_id, atributos_extra (json), activo
- Almacen: id, empresa_id, nombre, ubicacion
- StockBalance: id, producto_id, almacen_id, cantidad_disponible, valor_promedio
- StockLedger: id, producto_id, almacen_id, tipo_mov, qty, costo_unitario, total_valor, referencia_tipo, referencia_id, usuario_id, created_at
- Venta: id, empresa_id, usuario_id, cliente_id, estado, total, impuestos, fecha, caja_id
- VentaLinea: id, venta_id, producto_id, qty, precio_unitario, descuento, impuestos_linea
- Factura: id, numero, venta_id, cliente_id, estado, total, impuestos, fecha_emision
- Cliente: id, nombre, documento_fiscal, direccion, telefono, email, limite_credito, lista_precios_id
- AjusteInventario: id, producto_id, almacen_id, qty, motivo, usuario_autorizador_id, fecha
- CajaSesion: id, usuario_id, caja_id, apertura_fecha, cierre_fecha, efectivo_inicial, efectivo_cierre, discrepancia
- Pago: id, venta_id, metodo, monto, referencia, fecha

---
EVENTOS SUGERIDOS
- inventory.movement.created
- sales.sale.created
- invoices.invoice.issued
- users.session.opened/closed
- alerts.stock.low

---
APIs SUGERIDAS (REST)
- Productos: GET/POST/PUT /api/v1/empresas/{e}/productos
- Stock: POST /stock/entradas, POST /stock/salidas, GET /stock/{producto}
- Ventas: POST /ventas (atomiza venta+stock+pago), GET /ventas/{id}
- Facturas: POST /facturas, POST /facturas/{id}/anular
- Clientes: CRUD /clientes
- Usuarios/Roles: CRUD /usuarios, /roles
- Reportes: /reportes/stock, /reportes/rotacion

---
ESCENARIOS PRINCIPALES (resumen)
- Venta rápida: apertura caja -> agregar líneas -> pago -> venta atómica -> decrementar stock -> emitir comprobante.
- Entrada por compra: registrar recepción -> crear entradas en StockLedger -> actualizar balances.
- Devolución: seleccionar venta -> registrar devolución -> crear entrada y nota de crédito.

---
BACKLOG PRIORIZADO (MVP -> fases)
MVP:
- Productos CRUD, Almacenes, Ventas POS atómicas, Entradas/Salidas básicas, Usuarios/Roles mínimos, Reporte stock, Integridad transaccional.
Fase 1:
- Facturación básica, Clientes, Ajustes con autorización, Historial, Exportes.
Fase 2:
- Rotación de inventario, Alertas, Integración facturación electrónica, Optimización NFRs.

---
CRITERIOS DE ACEPTACIÓN GENERALES
- Pruebas unitarias e integración para flujos críticos (ventas, entradas, salidas, facturación).
- Documentación de API y ejemplos de uso para endpoints críticos.
- Pruebas de restauración de backups en staging.
- Revisión básica de seguridad (auth, permisos, auditoría).

---
SIGUIENTES PASOS RECOMENDADOS
- Validar políticas críticas: método de valoración por defecto, permitir stock negativo, facturación electrónica obligatoria.
- Priorizar desarrollo del MVP según necesidades comerciales.
- Diseñar migraciones SQL iniciales para Postgres y endpoints API para MVP.

