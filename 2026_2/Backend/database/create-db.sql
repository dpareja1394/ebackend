-- Crear usuario PostgreSQL con contraseña encriptada
CREATE USER backend_user WITH ENCRYPTED PASSWORD 'backend_password';

-- Crear base de datos
CREATE DATABASE backend_db OWNER backend_user;

-- Conectar a la base de datos y otorgar privilegios
\c backend_db;

-- Otorgar todos los privilegios en la base de datos al usuario
GRANT ALL PRIVILEGES ON DATABASE backend_db TO backend_user;

-- Otorgar privilegios en el esquema public
GRANT ALL PRIVILEGES ON SCHEMA public TO backend_user;

-- Otorgar privilegios por defecto para futuras tablas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO backend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO backend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON FUNCTIONS TO backend_user;
