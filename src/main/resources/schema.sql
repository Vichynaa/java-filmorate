CREATE TABLE IF NOT EXISTS genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mpa_status VARCHAR(12) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_name VARCHAR(40) NOT NULL,
    film_description TEXT,
    film_releaseDate DATE CHECK (film_releaseDate >= '1895-12-28'),
    film_duration INTEGER CHECK (film_duration > 0),
    mpa_id INTEGER,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(40) NOT NULL CHECK (user_email <> '' AND INSTR(user_email, '@') > 0),
    user_login VARCHAR(40) NOT NULL CHECK (user_login <> '' AND INSTR(user_login, ' ') = 0),
    user_name VARCHAR(40) NOT NULL,
    user_birthday DATE CHECK (user_birthday <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_like_id INTEGER,
    user_like_id INTEGER,
    FOREIGN KEY (film_like_id) REFERENCES films(id),
    FOREIGN KEY (user_like_id) REFERENCES users(id),
    UNIQUE (film_like_id, user_like_id)
);

CREATE TABLE IF NOT EXISTS friends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id INTEGER,
    to_user_id INTEGER,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_id INTEGER,
    genre_id INTEGER,
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    UNIQUE (film_id, genre_id)
);
