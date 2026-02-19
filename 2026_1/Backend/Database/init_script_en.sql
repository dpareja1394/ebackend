-- Table creation
CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL -- E.g.: CLIENT, ADMIN
);

CREATE TABLE user_account (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL REFERENCES role(id),
    status BOOLEAN DEFAULT TRUE
);

CREATE TABLE purchase_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL -- E.g.: PENDING, CONFIRMED, CANCELED
);

CREATE TABLE payment_method (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- E.g.: Credit Card, PayPal, Cash
    description TEXT
);

CREATE TABLE purchase (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES user_account(id),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method_id INT NOT NULL REFERENCES payment_method(id),
    status_id INT NOT NULL REFERENCES purchase_status(id)
);

CREATE TABLE purchase_detail (
    id               SERIAL PRIMARY KEY,
    purchase_id      INT NOT NULL REFERENCES purchase(id),
    identification   VARCHAR(100) NOT NULL,
    amount_paid      DECIMAL(10, 2) NOT NULL,
    card_number      VARCHAR(20) NOT NULL,
    transaction_id   VARCHAR(100)
);

CREATE TABLE rating (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL, -- E.g.: PG, PG-13, R
    description TEXT
);

CREATE TABLE genre (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE movie (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE movie_detail (
    id SERIAL PRIMARY KEY,
    movie_id INT NOT NULL REFERENCES movie(id),
    duration_minutes INT NOT NULL,
    rating_id INT REFERENCES rating(id),
    genre_id INT REFERENCES genre(id),
    release_date DATE,
    image_url TEXT,
    status BOOLEAN DEFAULT TRUE -- TRUE: active, FALSE: disabled
);

CREATE TABLE theater (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE screening (
    id SERIAL PRIMARY KEY,
    movie_id INT NOT NULL REFERENCES movie(id),
    theater_id INT NOT NULL REFERENCES theater(id),
    date_time TIMESTAMP NOT NULL
);

CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    screening_id INT NOT NULL REFERENCES screening(id),
    purchase_id INT NOT NULL REFERENCES purchase(id),
    seat_number VARCHAR(10) NOT NULL,
    price NUMERIC DEFAULT 0 NOT NULL,
    UNIQUE (screening_id, seat_number)
);

-- Additional indexes
CREATE INDEX idx_user_email ON user_account(email);
CREATE INDEX idx_movie_title ON movie(title);
CREATE INDEX idx_screening_datetime ON screening(date_time);
CREATE INDEX idx_ticket_seat ON ticket(screening_id, seat_number);
CREATE INDEX idx_purchase_user ON purchase(user_id);
CREATE INDEX idx_screening_theater ON screening(theater_id);
CREATE INDEX idx_movie_genre ON movie_detail(genre_id);
CREATE INDEX idx_movie_rating ON movie_detail(rating_id);
