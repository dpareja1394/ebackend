INSERT INTO role (name) VALUES ('CLIENTE');
INSERT INTO role (name) VALUES ('ADMIN');

INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Juan', 'Pérez', 'juan.perez@example.com', '1234567890', 'password123', 1, True);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Ana', 'Gómez', 'ana.gomez@example.com', '0987654321', 'password456', 2, True);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Daniel', 'Pareja', 'dpareja1394@gmail.com', '3003468834', '101234', 1, True);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Laura', 'González', 'laura.gonzalez@example.com', '3001112233', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Carlos', 'Ramírez', 'carlos.ramirez@example.com', '3002223344', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('María', 'López', 'maria.lopez@example.com', '3003334455', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Andrés', 'Torres', 'andres.torres@example.com', '3004445566', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Sofía', 'Martínez', 'sofia.martinez@example.com', '3005556677', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Julián', 'Castro', 'julian.castro@example.com', '3006667788', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Camila', 'Vargas', 'camila.vargas@example.com', '3007778899', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Felipe', 'Morales', 'felipe.morales@example.com', '3008889900', '101234', 1, TRUE);
INSERT INTO user_account (first_name, last_name, email, phone, password, role_id, status) VALUES ('Valentina', 'Ríos', 'valentina.rios@example.com', '3009990011', '101234', 1, TRUE);


INSERT INTO purchase_status (name) VALUES ('PENDIENTE');
INSERT INTO purchase_status (name) VALUES ('CONFIRMADA');
INSERT INTO purchase_status (name) VALUES ('CANCELADA');

INSERT INTO payment_method (name, description) VALUES ('Tarjeta de crédito', 'Pago con tarjeta de crédito');
INSERT INTO payment_method (name, description) VALUES ('PayPal', 'Pago con PayPal');
INSERT INTO payment_method (name, description) VALUES ('Efectivo', 'Pago en efectivo');

INSERT INTO purchase (user_id, date, payment_method_id, status_id) VALUES (1, '2025-05-01 10:00:00', 1, 1);
INSERT INTO purchase (user_id, date, payment_method_id, status_id) VALUES (2, '2025-05-02 11:00:00', 2, 2);

INSERT INTO purchase_detail (purchase_id, identification, amount_paid, card_number, transaction_id) VALUES (1, '1234567890', 37500, '**** **** **** 1234', 'TXN-20250507-001');
INSERT INTO purchase_detail (purchase_id, identification, amount_paid, card_number, transaction_id) VALUES (2, '202345', 76000, '**** **** **** 1234', 'TXN-20250507-001');

INSERT INTO rating (code, description) VALUES ('PG', 'Guía paternal sugerida');
INSERT INTO rating (code, description) VALUES ('PG-13', 'Guía paternal estricta');
INSERT INTO rating (code, description) VALUES ('R', 'Restringido');

INSERT INTO genre (name) VALUES ('Acción');
INSERT INTO genre (name) VALUES ('Comedia');
INSERT INTO genre (name) VALUES ('Terror');
INSERT INTO genre (name) VALUES ('Drama');
INSERT INTO genre (name) VALUES ('Romance');

INSERT INTO movie (title, description) VALUES ('Karate Kid: Leyendas', 'Luego de una tragedia familiar, el joven experto en kung fu, Li Fong, deja su hogar en Pekín y se traslada a Nueva York junto a su madre.');
INSERT INTO movie (title, description) VALUES ('Mamá reinventada', 'Tras la muerte de su padre, Marina decide llevar a Patricia, su mamá, a vivir con ella y su "roomie".');
INSERT INTO movie (title, description) VALUES ('La maldición del bosque', 'Tres hermanos regresan a la casa de su infancia, donde aparentemente sus padres fueron asesinados años atrás.');
INSERT INTO movie (title, description) VALUES ('Lilo & Stitch', 'Una fuerza maligna ronda el lugar y hará lo posible para impedirlo.');

INSERT INTO movie_detail (movie_id, duration_minutes, rating_id, genre_id, release_date, image_url, status) VALUES (1, 120, 1, 1, '2025-05-08', 'https://example.com/karate_kid.jpg', True);
INSERT INTO movie_detail (movie_id, duration_minutes, rating_id, genre_id, release_date, image_url, status) VALUES (2, 90, 2, 2, '2025-05-08', 'https://example.com/mama_reinventada.jpg', True);
INSERT INTO movie_detail (movie_id, duration_minutes, rating_id, genre_id, release_date, image_url, status) VALUES (3, 110, 3, 3, '2025-05-29', 'https://example.com/maldicion_bosque.jpg', True);
INSERT INTO movie_detail (movie_id, duration_minutes, rating_id, genre_id, release_date, image_url, status) VALUES (4, 95, 1, 1, '2025-05-22', 'https://example.com/lilo_stitch.jpg', True);

INSERT INTO theater (name, capacity, location) VALUES ('Sala 1', 100, 'Ubicación A');
INSERT INTO theater (name, capacity, location) VALUES ('Sala 2', 150, 'Ubicación B');

INSERT INTO screening (movie_id, theater_id, date_time) VALUES (1, 1, '2025-05-08 18:00:00');
INSERT INTO screening (movie_id, theater_id, date_time) VALUES (2, 2, '2025-05-08 20:00:00');
INSERT INTO screening (movie_id, theater_id, date_time) VALUES (3, 1, '2025-05-29 19:00:00');
INSERT INTO screening (movie_id, theater_id, date_time) VALUES (4, 2, '2025-05-22 17:00:00');

INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (1, 1, 'A1', 7500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (1, 1, 'A2', 7500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (1, 1, 'A3', 7500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (1, 1, 'A4', 7500);

INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B1', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B2', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B3', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B4', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B5', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B6', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B7', 8500);
INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (2, 2, 'B8', 8500);


INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (3, 1, 'A2', 7500);

INSERT INTO ticket (screening_id, purchase_id, seat_number, price) VALUES (4, 2, 'B2', 8000);