-- =========================================================
-- SCHEMA: Mini e-commerce (Catálogo, Carrito, Orden, Pago, Inventario)
-- Postgres | PKs SERIAL | Mayoria de edad en backend
-- =========================================================

-- -------------------------
-- 1) Catálogos base
-- -------------------------

CREATE TABLE document_types (
  id          SERIAL PRIMARY KEY,
  code        TEXT NOT NULL UNIQUE,   -- ej: CC, TI, CE, PAS
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

  birth_date        DATE NOT NULL,    -- validar +18 en backend

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

-- -------------------------
-- 2) Catálogo: categorías y productos
-- -------------------------

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

-- Muchos-a-muchos producto<->categoría (más flexible)
CREATE TABLE product_categories (
  product_id  INT NOT NULL,
  category_id INT NOT NULL,
  PRIMARY KEY (product_id, category_id),

  CONSTRAINT fk_pc_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_pc_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- Índices para filtros
CREATE INDEX idx_products_available ON products(available);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_product_categories_category ON product_categories(category_id, product_id);

-- Full-text search (nombre + descripción)
CREATE INDEX idx_products_fts
  ON products
  USING GIN (to_tsvector('spanish', coalesce(name,'') || ' ' || coalesce(description,'')));

-- -------------------------
-- 3) Inventario
-- -------------------------

CREATE TABLE inventory (
  product_id  INT PRIMARY KEY,
  stock       INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT fk_inventory_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Auditoría de inventario (opcional pero recomendada)
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

-- (NOTA) La FK a orders requiere que orders exista antes.
-- Como orders se crea más abajo, haremos el CREATE de inventory_movements
-- después de orders. Si pegaste todo de una, comenta esta tabla y úsala al final.

-- -------------------------
-- 4) Carrito
-- -------------------------

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

-- -------------------------
-- 5) Órdenes (Checkout)
-- -------------------------

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

-- -------------------------
-- 6) Pago simulado + idempotencia
-- -------------------------

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

-- Misma request de pago no se duplica
CREATE UNIQUE INDEX uq_payments_idempotency_key
  ON payments(idempotency_key);

-- Solo 1 pago SUCCEEDED por orden (evita doble pago / doble descuento)
CREATE UNIQUE INDEX uq_one_success_payment_per_order
  ON payments(order_id)
  WHERE status = 'SUCCEEDED';

CREATE INDEX idx_payments_order ON payments(order_id);

-- -------------------------
-- 7) Inventario_movements (ahora sí, con FK a orders)
-- -------------------------

-- -------------------------
-- 8) Seeds mínimos (opcional)
-- -------------------------

INSERT INTO document_types (code, name) VALUES
('CC', 'Cédula de ciudadanía'),
('TI', 'Tarjeta de identidad'),
('CE', 'Cédula de extranjería'),
('PAS', 'Pasaporte')
ON CONFLICT (code) DO NOTHING;
