-- ELIMINAR TODO
DROP TABLE IF EXISTS inventory_movements;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS document_types;
-- CREAR TABLAS
CREATE TABLE document_types (
  id          SERIAL PRIMARY KEY,
  code        TEXT NOT NULL UNIQUE,  
  name        TEXT NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE users (
  id                SERIAL PRIMARY KEY,

  full_name         TEXT NOT NULL,
  phone             TEXT,
  email             TEXT NOT NULL UNIQUE,

  document_type_id  INT NOT NULL,
  document_number   TEXT NOT NULL,

  birth_date        DATE NOT NULL,

  country           TEXT NOT NULL,
  address           TEXT,

  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_users_document_type
    FOREIGN KEY (document_type_id)
    REFERENCES document_types(id)
    ON DELETE RESTRICT,

  CONSTRAINT uq_users_document UNIQUE (document_type_id, document_number)
);

CREATE INDEX idx_users_document ON users(document_type_id, document_number);
CREATE INDEX idx_users_country ON users(country);

CREATE TABLE categories (
  id         SERIAL PRIMARY KEY,
  name       TEXT NOT NULL,
  parent_id  INT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_categories_parent
    FOREIGN KEY (parent_id) REFERENCES categories(id)
    ON DELETE SET NULL
);

CREATE TABLE products (
  id           SERIAL PRIMARY KEY,
  name         TEXT NOT NULL,
  description  TEXT,
  price        NUMERIC(12,2) NOT NULL CHECK (price >= 0),
  available    BOOLEAN NOT NULL DEFAULT TRUE,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE product_categories (
  product_id  INT NOT NULL,
  category_id INT NOT NULL,
  PRIMARY KEY (product_id, category_id),

  CONSTRAINT fk_pc_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_pc_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

CREATE INDEX idx_products_available ON products(available);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_product_categories_category ON product_categories(category_id, product_id);
CREATE INDEX idx_products_fts
  ON products
  USING GIN (to_tsvector('spanish', coalesce(name,'') || ' ' || coalesce(description,'')));

CREATE TABLE inventory (
  product_id  INT PRIMARY KEY,
  stock       INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_inventory_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);


CREATE TABLE carts (
  id         SERIAL PRIMARY KEY,
  user_id    INT NOT NULL,
  status     TEXT NOT NULL CHECK (status IN ('ACTIVE','CHECKED_OUT','ABANDONED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_carts_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_active_cart_per_user
  ON carts(user_id)
  WHERE status = 'ACTIVE';

CREATE INDEX idx_carts_user_status ON carts(user_id, status);

CREATE TABLE cart_items (
  id         SERIAL PRIMARY KEY,
  cart_id    INT NOT NULL,
  product_id INT NOT NULL,
  quantity   INT NOT NULL CHECK (quantity > 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
  CONSTRAINT fk_cart_items_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,

  CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product ON cart_items(product_id);

CREATE TABLE orders (
  id            SERIAL PRIMARY KEY,
  user_id       INT NOT NULL,
  status        TEXT NOT NULL CHECK (status IN ('CREATED','PAID','CANCELLED')),
  total_amount  NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
  currency      TEXT NOT NULL DEFAULT 'COP',
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  paid_at       TIMESTAMPTZ NULL,
  cancelled_at  TIMESTAMPTZ NULL,

  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_orders_user_created_at ON orders(user_id, created_at DESC);
CREATE INDEX idx_orders_status_created_at ON orders(status, created_at DESC);

CREATE TABLE order_items (
  id                  SERIAL PRIMARY KEY,
  order_id            INT NOT NULL,
  product_id          INT NOT NULL,
  quantity            INT NOT NULL CHECK (quantity > 0),
  unit_price_snapshot NUMERIC(12,2) NOT NULL CHECK (unit_price_snapshot >= 0),
  line_total          NUMERIC(12,2) NOT NULL CHECK (line_total >= 0),
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_order_product ON order_items(order_id, product_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE payments (
  id              SERIAL PRIMARY KEY,
  order_id        INT NOT NULL,
  status          TEXT NOT NULL CHECK (status IN ('SUCCEEDED','FAILED')),
  provider_ref    TEXT,
  idempotency_key TEXT NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_payments_order
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_payments_idempotency_key
  ON payments(idempotency_key);

CREATE UNIQUE INDEX uq_one_success_payment_per_order
  ON payments(order_id)
  WHERE status = 'SUCCEEDED';

CREATE INDEX idx_payments_order ON payments(order_id);

CREATE TABLE inventory_movements (
  id         SERIAL PRIMARY KEY,
  product_id INT NOT NULL,
  order_id   INT NULL,
  type       TEXT NOT NULL CHECK (type IN ('DEBIT','CREDIT','RESERVE','RELEASE')),
  qty        INT NOT NULL CHECK (qty > 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_inv_mov_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
  CONSTRAINT fk_inv_mov_order
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
);

CREATE INDEX idx_inventory_movements_product_created_at
  ON inventory_movements(product_id, created_at DESC);

CREATE INDEX idx_inventory_movements_order
  ON inventory_movements(order_id);

-- INSERTS
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

INSERT INTO inventory (product_id, stock) VALUES
(1, 10),
(2, 5), 
(3, 20),
(4, 50),
(5, 8);

INSERT INTO carts (user_id, status)
VALUES (1, 'ACTIVE');   -- id 1

INSERT INTO carts (user_id, status)
VALUES (2, 'ACTIVE');

INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 4, 2);

INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(2, 2, 1),
(2, 5, 1);

INSERT INTO orders (user_id, status, total_amount)
VALUES (1, 'CREATED', 4440000); -- id 1

INSERT INTO orders (user_id, status, total_amount)
VALUES (2, 'CREATED', 4480000); -- id 2

INSERT INTO order_items (
  order_id, product_id, quantity,
  unit_price_snapshot, line_total
) VALUES
(1, 1, 1, 4200000, 4200000),
(1, 4, 2, 120000, 240000);

INSERT INTO order_items (
  order_id, product_id, quantity,
  unit_price_snapshot, line_total
) VALUES
(2, 2, 1, 3500000, 3500000),
(2, 5, 1, 980000, 980000);

INSERT INTO payments (
  order_id, status, provider_ref, idempotency_key
) VALUES
(1, 'SUCCEEDED', 'PAY-TEST-0001', 'idem-order-1'),
(2, 'SUCCEEDED', 'PAY-TEST-0002', 'idem-order-2');

INSERT INTO inventory_movements (product_id, order_id, type, qty) VALUES
(1, 1, 'DEBIT', 1),
(4, 1, 'DEBIT', 2);

INSERT INTO inventory_movements (product_id, order_id, type, qty) VALUES
(2, 2, 'DEBIT', 1),
(5, 2, 'DEBIT', 1);

UPDATE inventory SET stock = stock - 1 WHERE product_id = 1;
UPDATE inventory SET stock = stock - 2 WHERE product_id = 4;
UPDATE inventory SET stock = stock - 1 WHERE product_id = 2;
UPDATE inventory SET stock = stock - 1 WHERE product_id = 5;
