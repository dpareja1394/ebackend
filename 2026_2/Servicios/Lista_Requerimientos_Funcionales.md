# Lista de Requerimientos Funcionales

**Sistema de Gestión de Compra-Venta de Vehículos — Concesionaria (Colombia)**

## GU — Gestión de Usuarios y Accesos

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-GU-01 | Registro y autenticación de usuarios internos | Registro y login de usuarios internos (asesor, gerente, admin) con rol asignado. | Admin, Asesor, Gerente Ventas | Alta |
| RF-GU-02 | Gestión de roles y permisos | Creación de roles y asignación de permisos por módulo (lectura/escritura/aprobación). | Admin | Alta |
| RF-GU-03 | Registro y autenticación de comprador | Registro y autenticación de compradores con verificación de correo/celular. | Comprador | Alta |
| RF-GU-04 | Recuperación de contraseña | Restablecimiento de contraseña vía enlace de un solo uso enviado al correo. | Comprador, Asesor, Gerente Ventas, Admin | Media |

## GV — Gestión de Inventario de Vehículos

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-GV-01 | Registro de vehículo en inventario | Alta de vehículo (nuevo/usado) en inventario con VIN, ficha básica y precio. | Asesor, Admin | Alta |
| RF-GV-02 | Publicación de vehículo en catálogo | Publicación del vehículo verificado en el catálogo público con ficha e imágenes. | Asesor, Admin | Alta |
| RF-GV-03 | Actualización del estado del vehículo en su ciclo de vida | Control del ciclo de estados del vehículo (verificación → publicado → vendido, etc.). | Asesor, Admin, Sistema | Alta |
| RF-GV-04 | Carga de galería de imágenes y ficha técnica | Carga de galería de imágenes y ficha técnica del vehículo. | Asesor | Media |
| RF-GV-05 | Baja o retiro de vehículo del catálogo | Retiro de un vehículo del catálogo sin perder su historial. | Asesor, Admin | Media |

## BF — Búsqueda y Filtros

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-BF-01 | Búsqueda de vehículos por criterios | Búsqueda de vehículos publicados con filtros combinables (marca, precio, año, etc.). | Comprador | Alta |
| RF-BF-02 | Comparador de vehículos | Comparación lado a lado de hasta 3 vehículos. | Comprador | Baja |
| RF-BF-03 | Guardar búsquedas y alertas de disponibilidad | Guardar criterios de búsqueda y recibir alerta de nuevas coincidencias. | Comprador, Sistema | Baja |

## CF — Cotización y Financiamiento

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-CF-01 | Generación de cotización | Generación de cotización formal con impuestos, accesorios y vigencia definida. | Comprador, Asesor | Alta |
| RF-CF-02 | Simulador de crédito vehicular | Simulador de cuota mensual de crédito según cuota inicial y plazo. | Comprador | Media |
| RF-CF-03 | Envío de solicitud de crédito a entidad financiera | Envío del expediente digital de crédito a la(s) entidad(es) financiera(s). | Comprador, Asesor, Ent. Financiera | Alta |
| RF-CF-04 | Registro de respuesta de aprobación o rechazo de crédito | Registro de la decisión de crédito (aprobado/rechazado) y su efecto en la venta. | Ent. Financiera, Sistema, Asesor | Alta |

## PV — Proceso de Venta

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-PV-01 | Registro de reserva de vehículo (apartado) | Reserva/apartado de un vehículo a nombre de un comprador con vigencia limitada. | Comprador, Asesor | Alta |
| RF-PV-02 | Asignación de asesor comercial a oportunidad de venta | Asignación automática o manual de un asesor a cada oportunidad de venta. | Gerente Ventas, Admin, Sistema | Media |
| RF-PV-03 | Registro de negociación y descuentos | Registro de negociación y descuentos dentro del rango autorizado al asesor. | Asesor | Alta |
| RF-PV-04 | Aprobación gerencial de descuentos especiales | Aprobación gerencial de descuentos que exceden el rango estándar. | Gerente Ventas, Asesor | Media |
| RF-PV-05 | Cierre formal de venta | Cierre formal de la venta al cumplirse pago y documentación. | Asesor, Sistema | Alta |

## DL — Documentación Legal y Trámites

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-DL-01 | Carga y validación de documentos del vehículo | Carga y validación de documentos obligatorios del vehículo (SOAT, tecnomecánica, etc.). | Asesor, Admin | Alta |
| RF-DL-02 | Verificación de propiedad y estado legal (RUNT) | Verificación del estado legal del vehículo usado (prendas, embargos, hurto) vía RUNT. | Asesor, Admin, Sistema | Alta |
| RF-DL-03 | Generación de contrato de compraventa | Generación automática del contrato de compraventa. | Sistema, Asesor | Alta |
| RF-DL-04 | Gestión de traspaso de propiedad | Seguimiento del trámite de traspaso de propiedad ante tránsito. | Asesor, Admin | Media |

## PG — Pagos

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-PG-01 | Registro de pago inicial / cuota inicial | Registro del pago inicial (transferencia o pago en línea). | Comprador, Asesor | Alta |
| RF-PG-02 | Integración con pasarela de pagos | Integración con pasarela de pagos (tarjeta/PSE) para pagos en línea. | Comprador, Sistema | Alta |
| RF-PG-03 | Registro de pago de saldo o desembolso de crédito | Registro del pago de saldo o desembolso del crédito aprobado. | Asesor, Ent. Financiera | Alta |
| RF-PG-04 | Generación de comprobante y factura electrónica | Generación automática de factura electrónica conforme a la DIAN. | Sistema | Alta |

## PS — Postventa

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-PS-01 | Registro de garantía del vehículo | Registro automático de la garantía del vehículo vendido (fábrica o concesionaria). | Sistema, Asesor | Media |
| RF-PS-02 | Agendamiento de servicio postventa / mantenimiento | Agendamiento de citas de mantenimiento/servicio postventa. | Comprador, Asesor | Baja |
| RF-PS-03 | Gestión de reclamos y PQRS | Radicación y seguimiento de PQRS del comprador. | Comprador, Asesor, Admin | Media |

## NT — Notificaciones

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-NT-01 | Notificaciones automáticas de estado de proceso | Notificaciones automáticas ante eventos clave (reserva, crédito, pago, venta, cita). | Sistema, Comprador, Asesor | Alta |
| RF-NT-02 | Notificaciones de nuevas publicaciones según alertas guardadas | Alerta de nuevas publicaciones que coinciden con búsquedas guardadas. | Sistema, Comprador | Baja |

## RA — Reportes y Administración

| Código | Nombre | Descripción | Actor(es) | Prioridad |
|---|---|---|---|---|
| RF-RA-01 | Dashboard gerencial de ventas | Dashboard gerencial con indicadores de ventas y conversión, filtrable por periodo. | Gerente Ventas, Admin | Media |
| RF-RA-02 | Reporte de inventario y rotación | Reporte de inventario y alertas de vehículos con baja rotación. | Gerente Ventas, Admin | Baja |
| RF-RA-03 | Configuración de parámetros del sistema | Configuración de parámetros del negocio (descuentos, vigencias, tasas, plantillas). | Admin | Media |
| RF-RA-04 | Auditoría de acciones de usuarios | Log de auditoría inmutable de acciones críticas de los usuarios. | Admin, Sistema | Alta |
