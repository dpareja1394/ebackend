CREATE TABLE paises (
  id SERIAL PRIMARY KEY,
  codigo VARCHAR(45),
  nombre VARCHAR(45),
  descripcion VARCHAR(45)
);


CREATE TABLE ciudades (
  id SERIAL PRIMARY KEY,
  idpais INT NOT NULL,
  nombre VARCHAR(45),
  descripcion VARCHAR(45),
  latitud VARCHAR(45),
  longitud VARCHAR(45),
  CONSTRAINT idpais_fk
    FOREIGN KEY (idpais)
    REFERENCES Paises (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);


CREATE TABLE aeropuertos (
  id SERIAL PRIMARY KEY,
  idciudades INT NOT NULL,
  nombre VARCHAR(45),
  descripcion VARCHAR(45),
  CONSTRAINT idciudades_fk
    FOREIGN KEY (idciudades)
    REFERENCES Ciudades (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);



CREATE TABLE aviones (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(45),
  descripcion VARCHAR(45),
  referencia VARCHAR(45),
  tipo VARCHAR(45),
  capacidad INT
);



CREATE TABLE asientos (
  id SERIAL PRIMARY KEY,
  fila VARCHAR(5),
  silla VARCHAR(5)
);


CREATE TABLE personas (
  id SERIAL PRIMARY KEY,
  tipodocumento VARCHAR(45),
  numerodocumento VARCHAR(45),
  nombre VARCHAR(45),
  apellido VARCHAR(45),
  telefono VARCHAR(45),
  correo VARCHAR(45)
);


CREATE TABLE vuelos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(45),
  descripcion VARCHAR(45),
  idaeorigen INT NOT NULL,
  fechasalida TIMESTAMP NOT NULL,
  idaedestino INT NOT NULL,
  fechallegada TIMESTAMP NOT NULL);



-- Table Aviones_has_AsientosAvion
CREATE TABLE asientosxavion (
  id SERIAL PRIMARY KEY,
  idaviones INT NOT NULL,
  idasientos INT NOT NULL,
  estado VARCHAR(1),
  CONSTRAINT idaviones_fk
    FOREIGN KEY (idaviones)
    REFERENCES Aviones (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idasientos_fk
    FOREIGN KEY (idasientos)
    REFERENCES asientos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);



-- Table DetallesVuelo
CREATE TABLE escalas (
  id SERIAL PRIMARY KEY,
  idvuelos INT NOT NULL,
  idaviones INT NOT NULL,
  idaeorigen INT NOT NULL,
  fechasalida TIMESTAMP NOT NULL,
  idaedestino INT NOT NULL,
  fechallegada TIMESTAMP NOT NULL,
  CONSTRAINT idvuelos_pk
    FOREIGN KEY (idvuelos)
    REFERENCES Vuelos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idaviones_pk
    FOREIGN KEY (idaviones)
    REFERENCES aviones (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idaeorigen_pk
    FOREIGN KEY (idaeorigen)
    REFERENCES aeropuertos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idaedestino_pk
    FOREIGN KEY (idaedestino)
    REFERENCES aeropuertos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);


CREATE TABLE reservas (
  id SERIAL PRIMARY KEY,
  idpersonas INT NOT NULL,
  idvuelos INT NOT NULL,
  fecha TIMESTAMP NOT NULL,
  idasxavion INT NOT NULL,
  CONSTRAINT idpersonas_fk
    FOREIGN KEY (idpersonas)
    REFERENCES Personas (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idvuelos_fk1
    FOREIGN KEY (idvuelos)
    REFERENCES Vuelos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT idasxavion_FK
    FOREIGN KEY (idasxavion)
    REFERENCES asientos (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);