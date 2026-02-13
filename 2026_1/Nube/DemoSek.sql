CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    imagen TEXT, -- URL o ruta de la imagen
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invitado (
    id_invitado SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO producto (nombre, descripcion, precio, stock, imagen)
VALUES 
('Silla Gamer', 'Silla ergonómica reclinable', 650000.00, 8, 'https://mipagina.com/img/silla-gamer.jpg'),
('Audífonos', 'Audífonos con cancelación de ruido', 180000.00, 20, 'https://mipagina.com/img/audifonos.jpg'),
('Webcam HD', 'Cámara 1080p para streaming', 220000.00, 12, 'https://mipagina.com/img/webcam.jpg');

INSERT INTO invitado (nombre, email, telefono)
VALUES ('Carlos Pérez', 'carlos.perez@gmail.com', '3001234567');

INSERT INTO invitado (nombre, email, telefono)
VALUES ('Laura Gómez', 'laura.gomez@hotmail.com', '3019876543');

INSERT INTO invitado (nombre, email, telefono)
VALUES ('Andrés Martínez', 'andres.martinez@yahoo.com', '3024567890');