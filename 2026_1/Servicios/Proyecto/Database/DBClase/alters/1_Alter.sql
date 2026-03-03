-- Eliminar la primary key compuesta existente
ALTER TABLE product_categories
DROP CONSTRAINT product_categories_pkey;

-- Agregar columna id tipo SERIAL
ALTER TABLE product_categories
ADD COLUMN id SERIAL;

-- Crear nueva primary key sobre id
ALTER TABLE product_categories
ADD CONSTRAINT product_categories_pkey PRIMARY KEY (id);

-- Mantener unicidad lógica entre product y category
ALTER TABLE product_categories
ADD CONSTRAINT uq_product_category UNIQUE (product_id, category_id);


---
-- Eliminar la primary key actual (product_id)
ALTER TABLE inventory
DROP CONSTRAINT inventory_pkey;

-- Agregar columna id tipo SERIAL
ALTER TABLE inventory
ADD COLUMN id SERIAL;

-- Crear nueva primary key sobre id
ALTER TABLE inventory
ADD CONSTRAINT inventory_pkey PRIMARY KEY (id);

-- Evitar que un producto tenga más de un registro de inventario
ALTER TABLE inventory
ADD CONSTRAINT uq_inventory_product UNIQUE (product_id);