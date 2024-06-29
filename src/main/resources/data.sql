CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
);

MERGE INTO genres (id, genre_name) VALUES 
(1, 'Комедия'), 
(2, 'Драма'), 
(3, 'Мультфильм'), 
(4, 'Триллер'), 
(5, 'Документальный'), 
(6, 'Боевик');

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT PRIMARY KEY,
    mpa_status VARCHAR(255) NOT NULL
);

MERGE INTO mpa (id, mpa_status) VALUES 
(1, 'G'), 
(2, 'PG'), 
(3, 'PG-13'), 
(4, 'R'), 
(5, 'NC-17');