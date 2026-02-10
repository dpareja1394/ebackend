-- =========================================================
-- 1) Tipos de documento
-- =========================================================

INSERT INTO document_types (code, name) VALUES
('CC',  'Cédula de ciudadanía'),
('TI',  'Tarjeta de identidad'),
('CE',  'Cédula de extranjería'),
('PAS', 'Pasaporte');

-- =========================================================
-- 2) Usuarios
-- =========================================================

INSERT INTO users (
  full_name, phone, email,
  document_type_id, document_number,
  birth_date, country, address
) VALUES
('Juan Pérez', '3001234567', 'juan.perez@mail.com',
  1, '123456789', '1995-05-10', 'Colombia', 'Calle 10 #20-30'),

('María Gómez', '3109876543', 'maria.gomez@mail.com',
  1, '987654321', '1998-08-22', 'Colombia', 'Carrera 45 #12-50'),

('Carlos Ramírez', '3205558899', 'carlos.r@mail.com',
  3, 'CE-556677', '1990-03-15', 'Colombia', 'Av. Siempre Viva 742');

-- =========================================================
-- 3) Categorías
-- =========================================================

INSERT INTO categories (name) VALUES
('Tecnología'),   -- id 1
('Hogar'),        -- id 2
('Deportes');     -- id 3

-- Subcategorías
INSERT INTO categories (name, parent_id) VALUES
('Celulares', 1),        -- id 4
('Computadores', 1),     -- id 5
('Electrodomésticos', 2);

-- =========================================================
-- 4) Productos
-- =========================================================

INSERT INTO products (name, description, price, available) VALUES
('iPhone 14', 'Smartphone Apple 128GB', 4200000, TRUE),          -- id 1
('Laptop Lenovo', 'Core i5, 16GB RAM, SSD 512GB', 3500000, TRUE),-- id 2
('Licuadora Oster', 'Licuadora 2 velocidades', 450000, TRUE),   -- id 3
('Balón de fútbol', 'Balón profesional tamaño 5', 120000, TRUE),
('Monitor Samsung 27"', 'Monitor Full HD 75Hz', 980000, TRUE);

-- =========================================================
-- 5) Relación productos ↔ categorías
-- =========================================================

INSERT INTO product_categories (product_id, category_id) VALUES
(1, 4), -- iPhone -> Celulares
(2, 5), -- Laptop -> Computadores
(5, 5), -- Monitor -> Computadores
(3, 6), -- Licuadora -> Electrodomésticos
(4, 3); -- Balón -> Deportes

-- =========================================================
-- 6) Inventario
-- =========================================================

INSERT INTO inventory (product_id, stock) VALUES
(1, 10),  -- iPhone
(2, 5),   -- Laptop
(3, 20),  -- Licuadora
(4, 50),  -- Balón
(5, 8);   -- Monitor

-- =========================================================
-- 7) Carritos
-- =========================================================

-- Carrito activo para Juan
INSERT INTO carts (user_id, status)
VALUES (1, 'ACTIVE');   -- id 1

-- Carrito activo para María
INSERT INTO carts (user_id, status)
VALUES (2, 'ACTIVE');   -- id 2

-- =========================================================
-- 8) Items del carrito
-- =========================================================

-- Carrito de Juan
INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(1, 1, 1),  -- 1 iPhone
(1, 4, 2);  -- 2 Balones

-- Carrito de María
INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(2, 2, 1),  -- 1 Laptop
(2, 5, 1);  -- 1 Monitor

-- =========================================================
-- 9) Órdenes
-- =========================================================

-- Orden de Juan
INSERT INTO orders (user_id, status, total_amount)
VALUES (1, 'CREATED', 4440000); -- id 1

-- Orden de María
INSERT INTO orders (user_id, status, total_amount)
VALUES (2, 'CREATED', 4480000); -- id 2

-- =========================================================
-- 10) Items de órdenes (snapshot de precios)
-- =========================================================

-- Orden Juan
INSERT INTO order_items (
  order_id, product_id, quantity,
  unit_price_snapshot, line_total
) VALUES
(1, 1, 1, 4200000, 4200000),
(1, 4, 2, 120000, 240000);

-- Orden María
INSERT INTO order_items (
  order_id, product_id, quantity,
  unit_price_snapshot, line_total
) VALUES
(2, 2, 1, 3500000, 3500000),
(2, 5, 1, 980000, 980000);

-- =========================================================
-- 11) Pagos simulados (idempotentes)
-- =========================================================

INSERT INTO payments (
  order_id, status, provider_ref, idempotency_key
) VALUES
(1, 'SUCCEEDED', 'PAY-TEST-0001', 'idem-order-1'),
(2, 'SUCCEEDED', 'PAY-TEST-0002', 'idem-order-2');

-- =========================================================
-- 12) Movimientos de inventario
-- =========================================================

-- Orden Juan
INSERT INTO inventory_movements (product_id, order_id, type, qty) VALUES
(1, 1, 'DEBIT', 1),
(4, 1, 'DEBIT', 2);

-- Orden María
INSERT INTO inventory_movements (product_id, order_id, type, qty) VALUES
(2, 2, 'DEBIT', 1),
(5, 2, 'DEBIT', 1);

-- =========================================================
-- 13) Actualización de inventario (post-pago)
-- =========================================================

UPDATE inventory SET stock = stock - 1 WHERE product_id = 1;
UPDATE inventory SET stock = stock - 2 WHERE product_id = 4;
UPDATE inventory SET stock = stock - 1 WHERE product_id = 2;
UPDATE inventory SET stock = stock - 1 WHERE product_id = 5;

-- =========================================================
-- FIN INSERTS DE PRUEBA
-- =========================================================
