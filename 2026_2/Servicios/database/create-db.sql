-- Crear usuario PostgreSQL con contraseña encriptada
CREATE USER servicios_user WITH ENCRYPTED PASSWORD 'servicios_password';

-- Crear base de datos
CREATE DATABASE servicios_db OWNER servicios_user;

-- Conectar a la base de datos y otorgar privilegios
\c servicios_db;

-- Otorgar todos los privilegios en la base de datos al usuario
GRANT ALL PRIVILEGES ON DATABASE servicios_db TO servicios_user;

-- Otorgar privilegios en el esquema public
GRANT ALL PRIVILEGES ON SCHEMA public TO servicios_user;

-- Otorgar privilegios por defecto para futuras tablas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO servicios_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO servicios_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON FUNCTIONS TO servicios_user;
