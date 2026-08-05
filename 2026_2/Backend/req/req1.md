# Requisitos funcionales - Sistema de compraventa de vehículos

## 1. Usuarios y autenticación
1.1. El sistema deberá permitir el registro de usuarios con rol definido.
1.2. El sistema deberá permitir el inicio de sesión mediante usuario y contraseña.
1.3. El sistema deberá validar credenciales y mantener sesión activa mientras el usuario esté autenticado.
1.4. Solo usuarios autenticados podrán acceder a la gestión de clientes, vehículos, compras y ventas.

## 2. Gestión de clientes
2.1. El sistema deberá permitir crear, editar, ver y eliminar clientes.
2.2. Cada cliente deberá contener al menos: nombre, identificación, teléfono, correo electrónico y dirección.
2.3. El sistema deberá listar clientes con opción de búsqueda y filtrado básico.

## 3. Gestión de vehículos
3.1. El sistema deberá permitir registrar vehículos con su marca y modelo asociados.
3.2. Cada vehículo deberá contener al menos: placa, tipo de carrocería, año, color, estado y kilometraje.
3.3. El sistema deberá permitir editar y consultar los datos de cada vehículo.
3.4. El sistema deberá listar vehículos con opción de búsqueda y filtrado por marca, modelo y estado.

## 4. Marcas y modelos
4.1. El sistema deberá permitir crear, editar y eliminar marcas de vehículos.
4.2. El sistema deberá permitir crear, editar y eliminar modelos de vehículos asociados a una marca.
4.3. El sistema deberá asegurar que un modelo esté vinculado a una marca válida.

## 5. Compras de vehículos
5.1. El sistema deberá registrar compras de vehículos por parte de la empresa.
5.2. Cada compra deberá incluir: vehículo comprado, proveedor (o vendedor), fecha de matrícula, secretaría de movilidad donde fue matriculado y precio de compra.
5.3. El sistema deberá permitir consultar el historial de compras por vehículo y por fecha.

## 6. Ventas de vehículos
6.1. El sistema deberá registrar ventas de vehículos a clientes.
6.2. Cada venta deberá incluir: vehículo vendido, cliente comprador, fecha de traspaso, secretaría de movilidad donde se radicó el traspaso y precio de venta.
6.3. El sistema deberá permitir consultar el historial de ventas por cliente, vehículo y fechas.

## 7. Fechas y secretarías de movilidad
7.1. El sistema deberá almacenar la fecha de matrícula de cada vehículo registrado en compra.
7.2. El sistema deberá almacenar la secretaría de movilidad donde se matriculó el vehículo.
7.3. El sistema deberá almacenar la fecha de traspaso y la secretaría de movilidad donde se radicó la venta del vehículo.

## 8. Restricciones mínimas
8.1. No se deberán incluir funcionalidades fuera del ámbito de usuarios, login, clientes, vehículos, marca, modelo, compras y ventas.
8.2. El sistema deberá asegurar que solo los datos obligatorios mencionados sean necesarios para las operaciones básicas.
