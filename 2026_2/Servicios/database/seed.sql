-- ============================================================
-- Sistema de Gestión Compra-Venta de Vehículos — Concesionaria
-- Datos de prueba (seed)
-- ============================================================

-- ============================================================
-- ROLES Y PERMISOS
-- ============================================================

INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMIN',           'Administrador del sistema con acceso total'),
    ('ASESOR',          'Asesor comercial de la concesionaria'),
    ('GERENTE_VENTAS',  'Gerente del área de ventas'),
    ('COMPRADOR',       'Cliente comprador externo');

INSERT INTO permisos (modulo, accion, descripcion) VALUES
    ('GU', 'LECTURA',    'Ver usuarios y roles'),
    ('GU', 'ESCRITURA',  'Crear y editar usuarios y roles'),
    ('GU', 'APROBACION', 'Aprobar accesos especiales'),
    ('GV', 'LECTURA',    'Ver inventario de vehículos'),
    ('GV', 'ESCRITURA',  'Registrar y editar vehículos'),
    ('CF', 'LECTURA',    'Ver cotizaciones y créditos'),
    ('CF', 'ESCRITURA',  'Crear cotizaciones y enviar solicitudes'),
    ('CF', 'APROBACION', 'Aprobar descuentos especiales'),
    ('PV', 'LECTURA',    'Ver proceso de venta'),
    ('PV', 'ESCRITURA',  'Gestionar reservas y ventas'),
    ('PV', 'APROBACION', 'Aprobar cierres y anulaciones'),
    ('PG', 'LECTURA',    'Ver pagos y facturas'),
    ('PG', 'ESCRITURA',  'Registrar pagos'),
    ('RA', 'LECTURA',    'Ver reportes y auditoría'),
    ('RA', 'ESCRITURA',  'Configurar parámetros del sistema');

-- ADMIN: todos los permisos
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p WHERE r.nombre = 'ADMIN';

-- GERENTE_VENTAS
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p
WHERE r.nombre = 'GERENTE_VENTAS'
  AND (p.modulo IN ('GV','CF','PV','PG','RA') OR (p.modulo = 'GU' AND p.accion = 'LECTURA'));

-- ASESOR
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p
WHERE r.nombre = 'ASESOR'
  AND p.modulo IN ('GV','CF','PV','PG')
  AND p.accion IN ('LECTURA','ESCRITURA');

-- COMPRADOR: solo lectura GV y CF
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p
WHERE r.nombre = 'COMPRADOR'
  AND p.modulo IN ('GV','CF') AND p.accion = 'LECTURA';

-- ============================================================
-- USUARIOS
-- ============================================================
-- Nota: password_hash corresponde a BCrypt de "Password123!"

INSERT INTO usuarios (nombres, apellidos, email, celular, password_hash, tipo, tipo_documento, numero_documento, email_verificado, activo) VALUES
    ('Carlos',   'Mendoza',    'admin@concesionaria.com',    '3001000001', '$2a$12$dummyhash.admin000000000000', 'INTERNO',   'CC', '10000001', TRUE,  TRUE),
    ('Laura',    'Ríos',       'gerente@concesionaria.com',  '3001000002', '$2a$12$dummyhash.gerente00000000', 'INTERNO',   'CC', '10000002', TRUE,  TRUE),
    ('Andrés',   'Salinas',    'asesor1@concesionaria.com',  '3001000003', '$2a$12$dummyhash.asesor100000000', 'INTERNO',   'CC', '10000003', TRUE,  TRUE),
    ('Valentina','Torres',     'asesor2@concesionaria.com',  '3001000004', '$2a$12$dummyhash.asesor200000000', 'INTERNO',   'CC', '10000004', TRUE,  TRUE),
    ('Miguel',   'Herrera',    'comprador1@gmail.com',       '3101000001', '$2a$12$dummyhash.comprador100000', 'COMPRADOR', 'CC', '20000001', TRUE,  TRUE),
    ('Sofía',    'Castro',     'comprador2@gmail.com',       '3101000002', '$2a$12$dummyhash.comprador200000', 'COMPRADOR', 'CC', '20000002', TRUE,  TRUE),
    ('Julián',   'Mora',       'comprador3@gmail.com',       '3101000003', '$2a$12$dummyhash.comprador300000', 'COMPRADOR', 'CC', '20000003', FALSE, TRUE),
    ('Mariana',  'Ospina',     'comprador4@gmail.com',       '3101000004', '$2a$12$dummyhash.comprador400000', 'COMPRADOR', 'CC', '20000004', TRUE,  TRUE);

-- Asignación de roles
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email = 'admin@concesionaria.com'   AND r.nombre = 'ADMIN';
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email = 'gerente@concesionaria.com' AND r.nombre = 'GERENTE_VENTAS';
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email = 'asesor1@concesionaria.com' AND r.nombre = 'ASESOR';
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email = 'asesor2@concesionaria.com' AND r.nombre = 'ASESOR';
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email LIKE 'comprador%' AND r.nombre = 'COMPRADOR';

-- Token de recuperación de ejemplo (expirado intencionalmente)
INSERT INTO tokens_recuperacion (usuario_id, token, usado, fecha_expiracion)
SELECT id, encode(gen_random_bytes(32), 'hex'), FALSE, NOW() + INTERVAL '1 hour'
FROM usuarios WHERE email = 'comprador3@gmail.com';

-- ============================================================
-- MARCAS Y MODELOS
-- ============================================================

INSERT INTO marcas (nombre) VALUES
    ('Chevrolet'), ('Renault'), ('Toyota'), ('Mazda'), ('Kia'), ('Nissan'), ('Ford'), ('Hyundai');

INSERT INTO modelos (marca_id, nombre)
SELECT m.id, mod.nombre FROM marcas m
JOIN (VALUES
    ('Chevrolet', 'Spark'),
    ('Chevrolet', 'Onix'),
    ('Chevrolet', 'Tracker'),
    ('Renault',   'Kwid'),
    ('Renault',   'Sandero'),
    ('Renault',   'Duster'),
    ('Toyota',    'Corolla'),
    ('Toyota',    'RAV4'),
    ('Mazda',     'CX-5'),
    ('Mazda',     'Mazda3'),
    ('Kia',       'Picanto'),
    ('Kia',       'Sportage'),
    ('Nissan',    'Frontier'),
    ('Ford',      'Ranger'),
    ('Hyundai',   'Tucson'),
    ('Hyundai',   'Grand i10')
) AS mod(marca, nombre) ON m.nombre = mod.marca;

-- ============================================================
-- PARÁMETROS DEL SISTEMA
-- ============================================================

INSERT INTO parametros_sistema (clave, valor, tipo_dato, descripcion) VALUES
    ('descuento_maximo_asesor_pct',     '5',     'NUMBER',  'Porcentaje máximo de descuento que puede aplicar un asesor sin aprobación'),
    ('descuento_maximo_gerente_pct',    '15',    'NUMBER',  'Porcentaje máximo de descuento con aprobación gerencial'),
    ('vigencia_cotizacion_dias',        '15',    'NUMBER',  'Días de vigencia de una cotización'),
    ('vigencia_reserva_horas',          '48',    'NUMBER',  'Horas de vigencia de una reserva'),
    ('iva_porcentaje',                  '19',    'NUMBER',  'Porcentaje de IVA aplicado en Colombia'),
    ('moneda_base',                     'COP',   'STRING',  'Moneda del sistema'),
    ('email_remitente',                 'noreply@concesionaria.com', 'STRING', 'Email de origen para notificaciones'),
    ('runt_api_url',                    'https://api.runt.gov.co/consulta', 'STRING', 'Endpoint de consulta RUNT'),
    ('garantia_minima_usados_meses',    '3',     'NUMBER',  'Garantía mínima para vehículos usados en meses'),
    ('alerta_rotacion_dias',            '90',    'NUMBER',  'Días sin movimiento para alertar baja rotación');

-- ============================================================
-- ENTIDADES FINANCIERAS
-- ============================================================

INSERT INTO entidades_financieras (nombre, nit, endpoint_api) VALUES
    ('Bancolombia',      '890903938-8', 'https://api.bancolombia.com/credito-vehicular/v1'),
    ('Banco de Bogotá',  '860003166-8', 'https://api.bbogota.com/vehicular/solicitudes'),
    ('Davivienda',       '860034313-7', 'https://api.davivienda.com/creditos/vehicular'),
    ('BBVA Colombia',    '900429008-3', NULL);

-- ============================================================
-- VEHÍCULOS
-- ============================================================

INSERT INTO vehiculos (vin, placa, modelo_id, anio, tipo, color, kilometraje, precio_base, estado, descripcion, publicado_en, usuario_registra_id)
SELECT
    v.vin, v.placa, mo.id, v.anio, v.tipo, v.color, v.km, v.precio, v.estado, v.descripcion, v.publicado_en,
    (SELECT id FROM usuarios WHERE email = 'asesor1@concesionaria.com')
FROM (VALUES
    ('1G1ZD5ST0LF000001', NULL,       'Chevrolet', 'Spark',    2024, 'NUEVO',  'Rojo Brillante',  0,      52900000, 'PUBLICADO', 'Spark LT 2024, aire acondicionado, MP3',       NOW() - INTERVAL '10 days'),
    ('1G1ZD5ST0LF000002', NULL,       'Chevrolet', 'Tracker',  2024, 'NUEVO',  'Blanco Perla',    0,      98500000, 'PUBLICADO', 'Tracker Premier 2024, turbo 1.2L, full equipo', NOW() - INTERVAL '8 days'),
    ('VF1BS000164000001', NULL,       'Renault',   'Duster',   2024, 'NUEVO',  'Gris Platino',    0,      89900000, 'PUBLICADO', 'Duster Zen 2024, 4x4, cámara de reversa',     NOW() - INTERVAL '12 days'),
    ('VF1BS000164000002', NULL,       'Renault',   'Kwid',     2023, 'NUEVO',  'Azul Cobalto',    0,      47500000, 'VERIFICADO', 'Kwid Zen 2023 en verificación final',          NULL),
    ('JTDBR32E120000001', 'ABC-123',  'Toyota',    'Corolla',  2022, 'USADO',  'Negro Cosmos',    35000,  89000000, 'PUBLICADO', 'Corolla XEI 2022, único dueño, historial completo', NOW() - INTERVAL '5 days'),
    ('JTDBR32E120000002', 'DEF-456',  'Mazda',     'CX-5',     2021, 'USADO',  'Rojo Soul',       52000,  105000000,'PUBLICADO', 'CX-5 Grand Touring 2021, AWD, techo panorámico', NOW() - INTERVAL '7 days'),
    ('KNDJP3A55G7000001', NULL,       'Kia',       'Sportage', 2024, 'NUEVO',  'Verde Jungle',    0,      115000000,'PUBLICADO', 'Sportage GT Line 2024, híbrido, ADAS completo', NOW() - INTERVAL '3 days'),
    ('KNDJP3A55G7000002', 'GHI-789',  'Hyundai',   'Tucson',   2020, 'USADO',  'Plata Metálico',  78000,  82000000, 'PUBLICADO', 'Tucson 2020, cambio automático, Apple CarPlay',  NOW() - INTERVAL '15 days'),
    ('1FMHK8D81EGA00001', NULL,       'Ford',      'Ranger',   2024, 'NUEVO',  'Negro Azabache',  0,      145000000,'EN_VERIFICACION', 'Ranger Raptor 2024, V6 biturbo',          NULL),
    ('3N1CN7AP0KL000001', 'JKL-012',  'Nissan',    'Frontier', 2019, 'USADO',  'Blanco Glaciar',  95000,  78500000, 'RETIRADO',  'Frontier 4x4 2019, retirada temporalmente',  NULL)
) AS v(vin, placa, marca, modelo, anio, tipo, color, km, precio, estado, descripcion, publicado_en)
JOIN marcas ma ON ma.nombre = v.marca
JOIN modelos mo ON mo.marca_id = ma.id AND mo.nombre = v.modelo;

-- Fichas técnicas
INSERT INTO fichas_tecnicas (vehiculo_id, motor, transmision, combustible, cilindraje_cc, potencia_hp, num_puertas, capacidad_pasajeros, num_airbags)
SELECT v.id, ft.motor, ft.transmision, ft.combustible, ft.cilindraje, ft.potencia, ft.puertas, ft.pasajeros, ft.airbags
FROM vehiculos v
JOIN (VALUES
    ('1G1ZD5ST0LF000001', '1.2L Naturally Aspirated',  'Manual 5 vel',      'Gasolina', 1200, 83,  5, 5, 2),
    ('1G1ZD5ST0LF000002', '1.2L Turbo ECOTEC',         'Automático CVT',    'Gasolina', 1200, 133, 5, 5, 6),
    ('VF1BS000164000001', '1.6L Naturally Aspirated',  'Manual 6 vel',      'Gasolina', 1598, 115, 5, 5, 4),
    ('JTDBR32E120000001', '2.0L Dual VVT-i',           'Automático 6 vel',  'Gasolina', 1987, 152, 4, 5, 7),
    ('JTDBR32E120000002', '2.5L SKYACTIV-G',           'Automático 6 vel',  'Gasolina', 2488, 187, 5, 5, 6),
    ('KNDJP3A55G7000001', '1.6L T-GDI Híbrido',        'Automático 6 vel',  'Híbrido',  1598, 180, 5, 5, 7),
    ('KNDJP3A55G7000002', '2.0L MPI',                  'Automático 6 vel',  'Gasolina', 1999, 156, 5, 5, 6),
    ('1FMHK8D81EGA00001', '3.0L EcoBoost V6 Biturbo',  'Automático 10 vel', 'Gasolina', 2995, 400, 4, 5, 6)
) AS ft(vin, motor, transmision, combustible, cilindraje, potencia, puertas, pasajeros, airbags)
ON v.vin = ft.vin;

-- Imágenes (principal por cada vehículo publicado)
INSERT INTO imagenes_vehiculo (vehiculo_id, url, orden, es_principal)
SELECT v.id, 'https://storage.concesionaria.com/vehiculos/' || v.vin || '/img_01.jpg', 1, TRUE
FROM vehiculos v WHERE v.estado IN ('PUBLICADO', 'VERIFICADO', 'EN_VERIFICACION');

-- Documentos de vehículos usados
INSERT INTO documentos_vehiculo (vehiculo_id, tipo_documento, url, fecha_expedicion, fecha_vencimiento, validado, fecha_validacion, usuario_valida_id)
SELECT
    v.id,
    d.tipo,
    'https://storage.concesionaria.com/docs/' || v.vin || '/' || lower(d.tipo) || '.pdf',
    d.fecha_exp::DATE,
    d.fecha_venc::DATE,
    TRUE,
    NOW() - INTERVAL '2 days',
    (SELECT id FROM usuarios WHERE email = 'admin@concesionaria.com')
FROM vehiculos v
JOIN (VALUES
    ('JTDBR32E120000001', 'SOAT',            '2024-03-01', '2025-03-01'),
    ('JTDBR32E120000001', 'TECNOMECANICA',   '2024-01-15', '2026-01-15'),
    ('JTDBR32E120000001', 'TARJETA_PROPIEDAD','2022-05-10', NULL),
    ('JTDBR32E120000002', 'SOAT',            '2024-05-01', '2025-05-01'),
    ('JTDBR32E120000002', 'TECNOMECANICA',   '2023-11-20', '2025-11-20'),
    ('KNDJP3A55G7000002', 'SOAT',            '2024-04-10', '2025-04-10'),
    ('3N1CN7AP0KL000001', 'SOAT',            '2023-06-01', '2024-06-01')
) AS d(vin, tipo, fecha_exp, fecha_venc) ON v.vin = d.vin;

-- Verificaciones RUNT para usados
INSERT INTO verificaciones_runt (vehiculo_id, placa, tiene_prendas, tiene_embargos, reporte_hurto, estado_legal, usuario_consulta_id)
SELECT
    v.id, v.placa, r.prendas, r.embargos, r.hurto, r.estado,
    (SELECT id FROM usuarios WHERE email = 'asesor1@concesionaria.com')
FROM vehiculos v
JOIN (VALUES
    ('ABC-123', FALSE, FALSE, FALSE, 'LIBRE'),
    ('DEF-456', FALSE, FALSE, FALSE, 'LIBRE'),
    ('GHI-789', FALSE, FALSE, FALSE, 'LIBRE'),
    ('JKL-012', TRUE,  FALSE, FALSE, 'CON_PRENDA')
) AS r(placa, prendas, embargos, hurto, estado) ON v.placa = r.placa;

-- ============================================================
-- BÚSQUEDAS GUARDADAS
-- ============================================================

INSERT INTO busquedas_guardadas (usuario_id, nombre, criterios, alerta_activa)
SELECT u.id, b.nombre, b.criterios::JSONB, b.alerta
FROM usuarios u
JOIN (VALUES
    ('comprador1@gmail.com', 'SUVs nuevas hasta 120M',
     '{"tipo":"NUEVO","precio_max":120000000,"categoria":"SUV"}',          TRUE),
    ('comprador2@gmail.com', 'Sedanes usados 2020+',
     '{"tipo":"USADO","anio_min":2020,"categoria":"SEDAN","precio_max":95000000}', FALSE),
    ('comprador1@gmail.com', 'Camionetas 4x4',
     '{"tipo":"NUEVO","traccion":"4x4"}',                                  FALSE)
) AS b(email, nombre, criterios, alerta) ON u.email = b.email;

-- ============================================================
-- COTIZACIONES
-- ============================================================

INSERT INTO cotizaciones (codigo, vehiculo_id, comprador_id, asesor_id, precio_base, descuento, impuestos, precio_total, vigencia_hasta, estado)
SELECT
    c.codigo,
    (SELECT id FROM vehiculos WHERE vin = c.vin),
    (SELECT id FROM usuarios  WHERE email = c.comprador),
    (SELECT id FROM usuarios  WHERE email = c.asesor),
    c.precio_base, c.descuento, c.impuestos, c.total,
    (NOW() + (c.vigencia_dias || ' days')::INTERVAL)::DATE,
    c.estado
FROM (VALUES
    ('COT-2024-001', '1G1ZD5ST0LF000002', 'comprador1@gmail.com', 'asesor1@concesionaria.com', 98500000,  2000000, 18715000, 115215000, '14',  'ENVIADA'),
    ('COT-2024-002', 'JTDBR32E120000001', 'comprador2@gmail.com', 'asesor1@concesionaria.com', 89000000,  0,       0,        89000000,  '10',  'ACEPTADA'),
    ('COT-2024-003', 'KNDJP3A55G7000001', 'comprador1@gmail.com', 'asesor2@concesionaria.com', 115000000, 3000000, 21280000, 133280000, '15',  'BORRADOR'),
    ('COT-2024-004', 'JTDBR32E120000002', 'comprador4@gmail.com', 'asesor2@concesionaria.com', 105000000, 5000000, 19000000, 119000000, '-5',  'VENCIDA'),
    ('COT-2024-005', 'KNDJP3A55G7000002', 'comprador3@gmail.com', 'asesor1@concesionaria.com', 82000000,  0,       0,        82000000,  '12',  'ENVIADA')
) AS c(codigo, vin, comprador, asesor, precio_base, descuento, impuestos, total, vigencia_dias, estado);

-- Accesorios
INSERT INTO accesorios_cotizacion (cotizacion_id, descripcion, precio)
SELECT c.id, a.desc, a.precio::NUMERIC
FROM cotizaciones c
JOIN (VALUES
    ('COT-2024-001', 'Seguro de llantas 1 año',   350000),
    ('COT-2024-001', 'Tapetes originales Chevrolet', 280000),
    ('COT-2024-003', 'Protector de carrocería PPF', 1800000),
    ('COT-2024-003', 'Alarma con GPS incorporado',  650000)
) AS a(codigo, "desc", precio) ON c.codigo = a.codigo;

-- ============================================================
-- SOLICITUDES DE CRÉDITO
-- ============================================================

INSERT INTO solicitudes_credito (cotizacion_id, comprador_id, entidad_financiera_id, monto_solicitado, cuota_inicial, plazo_meses, estado, observaciones, fecha_respuesta)
SELECT
    c.id,
    (SELECT id FROM usuarios WHERE email = sc.comprador),
    (SELECT id FROM entidades_financieras WHERE nombre = sc.entidad),
    sc.monto, sc.cuota_inicial, sc.plazo, sc.estado, sc.obs, sc.fecha_resp
FROM cotizaciones c
JOIN (VALUES
    ('COT-2024-002', 'comprador2@gmail.com', 'Bancolombia',     71200000, 17800000, 60, 'APROBADA',  'Crédito aprobado al 12.5% E.A.',   NOW() - INTERVAL '3 days'),
    ('COT-2024-004', 'comprador4@gmail.com', 'Davivienda',      95000000, 24000000, 72, 'RECHAZADA', 'Capacidad de pago insuficiente',   NOW() - INTERVAL '10 days'),
    ('COT-2024-005', 'comprador3@gmail.com', 'Banco de Bogotá', 65600000, 16400000, 48, 'EN_ESTUDIO', NULL,                              NULL)
) AS sc(codigo, comprador, entidad, monto, cuota_inicial, plazo, estado, obs, fecha_resp)
ON c.codigo = sc.codigo;

-- ============================================================
-- OPORTUNIDADES DE VENTA
-- ============================================================

INSERT INTO oportunidades_venta (vehiculo_id, comprador_id, asesor_id, estado, origen)
SELECT
    (SELECT id FROM vehiculos WHERE vin = op.vin),
    (SELECT id FROM usuarios  WHERE email = op.comprador),
    (SELECT id FROM usuarios  WHERE email = op.asesor),
    op.estado, op.origen
FROM (VALUES
    ('JTDBR32E120000001', 'comprador2@gmail.com', 'asesor1@concesionaria.com', 'GANADA',            'WEB'),
    ('1G1ZD5ST0LF000002', 'comprador1@gmail.com', 'asesor1@concesionaria.com', 'COTIZACION_ENVIADA','WEB'),
    ('KNDJP3A55G7000001', 'comprador1@gmail.com', 'asesor2@concesionaria.com', 'EN_NEGOCIACION',    'PRESENCIAL'),
    ('JTDBR32E120000002', 'comprador4@gmail.com', 'asesor2@concesionaria.com', 'PERDIDA',           'TELEFONO'),
    ('KNDJP3A55G7000002', 'comprador3@gmail.com', 'asesor1@concesionaria.com', 'CREDITO_EN_TRAMITE','WEB'),
    ('VF1BS000164000001', 'comprador4@gmail.com', 'asesor2@concesionaria.com', 'NUEVA',             'WEB')
) AS op(vin, comprador, asesor, estado, origen);

-- ============================================================
-- RESERVAS
-- ============================================================

INSERT INTO reservas (vehiculo_id, oportunidad_id, comprador_id, asesor_id, monto_reserva, vigencia_hasta, estado)
SELECT
    (SELECT id FROM vehiculos WHERE vin = r.vin),
    (SELECT op.id FROM oportunidades_venta op
     JOIN vehiculos v ON v.id = op.vehiculo_id
     JOIN usuarios  u ON u.id = op.comprador_id
     WHERE v.vin = r.vin AND u.email = r.comprador LIMIT 1),
    (SELECT id FROM usuarios WHERE email = r.comprador),
    (SELECT id FROM usuarios WHERE email = r.asesor),
    r.monto, r.vigencia, r.estado
FROM (VALUES
    ('KNDJP3A55G7000001', 'comprador1@gmail.com', 'asesor2@concesionaria.com', 2000000, NOW() + INTERVAL '24 hours', 'ACTIVA'),
    ('KNDJP3A55G7000002', 'comprador3@gmail.com', 'asesor1@concesionaria.com', 1500000, NOW() + INTERVAL '36 hours', 'ACTIVA'),
    ('JTDBR32E120000001', 'comprador2@gmail.com', 'asesor1@concesionaria.com', 2000000, NOW() - INTERVAL '5 days',  'CONVERTIDA')
) AS r(vin, comprador, asesor, monto, vigencia, estado);

-- ============================================================
-- NEGOCIACIONES
-- ============================================================

INSERT INTO negociaciones (oportunidad_id, asesor_id, precio_ofertado, descuento_porcentaje, descuento_valor, requiere_aprobacion, estado_aprobacion, aprobador_id, observaciones)
SELECT
    (SELECT op.id FROM oportunidades_venta op
     JOIN vehiculos v ON v.id = op.vehiculo_id
     WHERE v.vin = ng.vin LIMIT 1),
    (SELECT id FROM usuarios WHERE email = ng.asesor),
    ng.precio_ofertado, ng.descuento_pct, ng.descuento_val,
    ng.req_aprobacion, ng.estado_aprobacion,
    CASE WHEN ng.req_aprobacion THEN (SELECT id FROM usuarios WHERE email = 'gerente@concesionaria.com') ELSE NULL END,
    ng.obs
FROM (VALUES
    ('JTDBR32E120000001', 'asesor1@concesionaria.com', 87500000, 1.69, 1500000, FALSE, NULL,       'Comprador solicitó rebaja por pago de contado'),
    ('KNDJP3A55G7000001', 'asesor2@concesionaria.com', 104500000, 9.13, 10500000, TRUE, 'APROBADA', 'Descuento especial aprobado por gerencia para cliente corporativo'),
    ('JTDBR32E120000002', 'asesor2@concesionaria.com', 98000000,  6.67, 7000000,  TRUE, 'RECHAZADA','Descuento rechazado, precio no rentable')
) AS ng(vin, asesor, precio_ofertado, descuento_pct, descuento_val, req_aprobacion, estado_aprobacion, obs);

-- ============================================================
-- VENTAS
-- ============================================================

INSERT INTO ventas (numero_venta, oportunidad_id, vehiculo_id, comprador_id, asesor_id, precio_final, forma_pago, solicitud_credito_id, estado, fecha_cierre)
SELECT
    vt.numero,
    (SELECT op.id FROM oportunidades_venta op
     JOIN vehiculos v ON v.id = op.vehiculo_id
     WHERE v.vin = vt.vin LIMIT 1),
    (SELECT id FROM vehiculos WHERE vin = vt.vin),
    (SELECT id FROM usuarios  WHERE email = vt.comprador),
    (SELECT id FROM usuarios  WHERE email = vt.asesor),
    vt.precio_final, vt.forma_pago,
    CASE WHEN vt.forma_pago = 'CREDITO'
         THEN (SELECT sc.id FROM solicitudes_credito sc
               JOIN cotizaciones co ON co.id = sc.cotizacion_id
               JOIN usuarios u ON u.id = sc.comprador_id
               WHERE u.email = vt.comprador AND sc.estado = 'APROBADA' LIMIT 1)
         ELSE NULL END,
    vt.estado, vt.fecha_cierre
FROM (VALUES
    ('VTA-2024-001', 'JTDBR32E120000001', 'comprador2@gmail.com', 'asesor1@concesionaria.com', 87500000,  'CREDITO', 'CERRADA', NOW() - INTERVAL '2 days')
) AS vt(numero, vin, comprador, asesor, precio_final, forma_pago, estado, fecha_cierre);

-- Actualizar estado del vehículo vendido
UPDATE vehiculos SET estado = 'VENDIDO' WHERE vin = 'JTDBR32E120000001';

-- ============================================================
-- CONTRATOS
-- ============================================================

INSERT INTO contratos (venta_id, numero_contrato, url_documento, firmado_comprador, firmado_asesor, fecha_firma)
SELECT
    v.id,
    'CTR-2024-001',
    'https://storage.concesionaria.com/contratos/CTR-2024-001.pdf',
    TRUE, TRUE,
    NOW() - INTERVAL '2 days'
FROM ventas v WHERE v.numero_venta = 'VTA-2024-001';

-- ============================================================
-- TRÁMITES DE TRASPASO
-- ============================================================

INSERT INTO tramites_traspaso (venta_id, estado, observaciones, usuario_gestiona_id)
SELECT
    v.id, 'EN_TRAMITE',
    'Expediente radicado en Secretaría de Tránsito de Medellín',
    (SELECT id FROM usuarios WHERE email = 'asesor1@concesionaria.com')
FROM ventas v WHERE v.numero_venta = 'VTA-2024-001';

-- ============================================================
-- PAGOS
-- ============================================================

INSERT INTO pagos (venta_id, tipo_pago, monto, medio_pago, estado, referencia_externa, fecha_pago)
SELECT v.id, p.tipo, p.monto::NUMERIC, p.medio, p.estado, p.ref, p.fecha_pago
FROM ventas v
JOIN (VALUES
    ('VTA-2024-001', 'CUOTA_INICIAL',    17800000, 'PSE',          'APROBADO', 'PSE-2024-88821', NOW() - INTERVAL '4 days'),
    ('VTA-2024-001', 'DESEMBOLSO_CREDITO', 69700000, 'DESEMBOLSO', 'APROBADO', 'BCO-2024-55500', NOW() - INTERVAL '2 days')
) AS p(venta, tipo, monto, medio, estado, ref, fecha_pago) ON v.numero_venta = p.venta;

-- Transacción pasarela (cuota inicial por PSE)
INSERT INTO transacciones_pasarela (pago_id, proveedor, referencia_pasarela, estado_pasarela, monto, respuesta_raw)
SELECT
    pg.id, 'Wompi', 'WOMPI-TXN-' || pg.referencia_externa,
    'APPROVED', pg.monto,
    '{"status":"APPROVED","payment_method":"PSE","bank":"Bancolombia"}'::JSONB
FROM pagos pg
WHERE pg.referencia_externa = 'PSE-2024-88821';

-- ============================================================
-- FACTURAS
-- ============================================================

INSERT INTO facturas (venta_id, numero_factura, cufe, subtotal, iva, total, estado)
SELECT
    v.id,
    'FE-2024-000001',
    'abc123def456' || encode(gen_random_bytes(16), 'hex'),
    73529412,
    13970588,
    87500000,
    'ACEPTADA'
FROM ventas v WHERE v.numero_venta = 'VTA-2024-001';

-- ============================================================
-- GARANTÍAS
-- ============================================================

INSERT INTO garantias (venta_id, tipo, descripcion, duracion_meses, fecha_inicio, fecha_fin, condiciones)
SELECT
    v.id, 'CONCESIONARIA',
    'Garantía de concesionaria sobre motor y caja',
    12,
    NOW()::DATE,
    (NOW() + INTERVAL '12 months')::DATE,
    'Cubre fallas de motor y transmisión bajo uso normal. Excluye desgaste por uso y daños por accidente.'
FROM ventas v WHERE v.numero_venta = 'VTA-2024-001';

-- ============================================================
-- CITAS DE SERVICIO
-- ============================================================

INSERT INTO citas_servicio (comprador_id, vehiculo_id, asesor_id, tipo_servicio, fecha_cita, estado, observaciones)
SELECT
    (SELECT id FROM usuarios  WHERE email = c.comprador),
    (SELECT id FROM vehiculos WHERE vin   = c.vin),
    (SELECT id FROM usuarios  WHERE email = c.asesor),
    c.tipo_servicio, c.fecha_cita, c.estado, c.obs
FROM (VALUES
    ('comprador2@gmail.com', 'JTDBR32E120000001', 'asesor1@concesionaria.com',
     'Revisión 1.000 km', NOW() + INTERVAL '20 days', 'AGENDADA', 'Primera revisión post-compra incluida en garantía'),
    ('comprador2@gmail.com', 'JTDBR32E120000001', 'asesor1@concesionaria.com',
     'Cambio de aceite 5.000 km', NOW() - INTERVAL '30 days', 'REALIZADA', NULL)
) AS c(comprador, vin, asesor, tipo_servicio, fecha_cita, estado, obs);

-- ============================================================
-- PQRS
-- ============================================================

INSERT INTO pqrs (numero_radicado, comprador_id, tipo, asunto, descripcion, venta_id, estado, usuario_asignado_id, respuesta, fecha_cierre)
SELECT
    p.radicado,
    (SELECT id FROM usuarios WHERE email = p.comprador),
    p.tipo, p.asunto, p.descripcion,
    (SELECT v.id FROM ventas v WHERE v.numero_venta = p.venta),
    p.estado,
    (SELECT id FROM usuarios WHERE email = p.asignado),
    p.respuesta, p.fecha_cierre
FROM (VALUES
    ('PQRS-2024-001', 'comprador2@gmail.com', 'PETICION',  'Solicitud manual propietario',
     'Requiero el manual de propietario en físico del vehículo adquirido.',
     'VTA-2024-001', 'RESUELTA', 'asesor1@concesionaria.com',
     'Manual enviado por mensajería el 2024-08-05.', NOW() - INTERVAL '15 days'),
    ('PQRS-2024-002', 'comprador1@gmail.com', 'QUEJA',     'Demora en respuesta de cotización',
     'Llevo 5 días esperando respuesta a mi cotización COT-2024-003 sin novedad.',
     NULL, 'EN_GESTION', 'asesor2@concesionaria.com',
     NULL, NULL)
) AS p(radicado, comprador, tipo, asunto, descripcion, venta, estado, asignado, respuesta, fecha_cierre);

-- ============================================================
-- NOTIFICACIONES
-- ============================================================

INSERT INTO notificaciones (usuario_id, tipo, titulo, cuerpo, canal, leida, referencia_tipo, referencia_id, fecha_envio)
SELECT
    (SELECT id FROM usuarios WHERE email = n.usuario),
    n.tipo, n.titulo, n.cuerpo, n.canal, n.leida,
    n.ref_tipo,
    CASE n.ref_tipo
        WHEN 'VENTA'    THEN (SELECT id FROM ventas       WHERE numero_venta    = n.ref_valor)
        WHEN 'RESERVA'  THEN (SELECT id FROM reservas     WHERE vehiculo_id     = (SELECT id FROM vehiculos WHERE vin = n.ref_valor))
        WHEN 'COTIZACION' THEN (SELECT id FROM cotizaciones WHERE codigo        = n.ref_valor)
        WHEN 'PQRS'     THEN (SELECT id FROM pqrs         WHERE numero_radicado = n.ref_valor)
        ELSE NULL
    END,
    NOW() - (n.hace_horas || ' hours')::INTERVAL
FROM (VALUES
    ('comprador2@gmail.com', 'VENTA',      'Venta cerrada exitosamente',
     'Su compra del Toyota Corolla 2022 ha sido registrada. Número de venta: VTA-2024-001.',
     'EMAIL', TRUE,  'VENTA',     'VTA-2024-001', '48'),
    ('comprador2@gmail.com', 'PAGO',       'Pago de cuota inicial confirmado',
     'Recibimos su pago de $17.800.000 como cuota inicial. Referencia: PSE-2024-88821.',
     'EMAIL', TRUE,  'VENTA',     'VTA-2024-001', '96'),
    ('comprador1@gmail.com', 'RESERVA',    'Reserva confirmada — Kia Sportage GT Line',
     'Su reserva está activa por 24 horas. Comuníquese con su asesor para continuar.',
     'IN_APP', FALSE, 'RESERVA',  'KNDJP3A55G7000001', '2'),
    ('comprador1@gmail.com', 'COTIZACION', 'Nueva cotización disponible',
     'Su cotización COT-2024-001 por $115.215.000 está lista. Vigencia: 14 días.',
     'EMAIL', FALSE, 'COTIZACION','COT-2024-001', '24'),
    ('comprador2@gmail.com', 'CITA',       'Cita de servicio agendada',
     'Su cita de revisión 1.000 km está programada. Le confirmaremos la fecha exacta.',
     'SMS',  FALSE, 'VENTA',     'VTA-2024-001', '1'),
    ('asesor1@concesionaria.com', 'PQRS',  'Nueva PQRS asignada',
     'Se le asignó la petición PQRS-2024-001 del cliente Miguel Herrera. Responder en máx. 3 días.',
     'IN_APP', TRUE, 'PQRS',     'PQRS-2024-001', '360')
) AS n(usuario, tipo, titulo, cuerpo, canal, leida, ref_tipo, ref_valor, hace_horas);

-- ============================================================
-- AUDITORÍA
-- ============================================================

INSERT INTO auditoria (usuario_id, accion, entidad, entidad_id, datos_anteriores, datos_nuevos, ip_origen)
SELECT
    (SELECT id FROM usuarios WHERE email = a.usuario),
    a.accion, a.entidad,
    CASE a.entidad
        WHEN 'vehiculos'  THEN (SELECT id FROM vehiculos WHERE vin         = a.ref)
        WHEN 'ventas'     THEN (SELECT id FROM ventas    WHERE numero_venta = a.ref)
        WHEN 'usuarios'   THEN (SELECT id FROM usuarios  WHERE email        = a.ref)
        ELSE NULL
    END,
    a.datos_ant::JSONB, a.datos_nuevo::JSONB, a.ip
FROM (VALUES
    ('admin@concesionaria.com', 'CREAR_USUARIO',     'usuarios',  'comprador1@gmail.com',  NULL, '{"email":"comprador1@gmail.com","tipo":"COMPRADOR"}', '181.55.10.22'),
    ('asesor1@concesionaria.com','REGISTRAR_VEHICULO','vehiculos', '1G1ZD5ST0LF000001',    NULL, '{"vin":"1G1ZD5ST0LF000001","estado":"INGRESADO"}',    '192.168.1.5'),
    ('admin@concesionaria.com', 'CAMBIAR_ESTADO',    'vehiculos', 'JTDBR32E120000001', '{"estado":"PUBLICADO"}', '{"estado":"VENDIDO"}', '192.168.1.3'),
    ('asesor1@concesionaria.com','CERRAR_VENTA',      'ventas',    'VTA-2024-001',      NULL, '{"numero_venta":"VTA-2024-001","estado":"CERRADA"}',   '192.168.1.5'),
    ('admin@concesionaria.com', 'ACTUALIZAR_PARAM',  'parametros_sistema', NULL, '{"clave":"iva_porcentaje","valor":"18"}', '{"clave":"iva_porcentaje","valor":"19"}', '192.168.1.3')
) AS a(usuario, accion, entidad, ref, datos_ant, datos_nuevo, ip);
