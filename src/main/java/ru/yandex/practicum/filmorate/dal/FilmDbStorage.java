package ru.yandex.practicum.filmorate.dal;

import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreRequest;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorageInterface;

import java.util.*;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmDbStorageInterface {
    private static final String FIND_ALL_QUERY =
            "SELECT f.id, f.film_name, f.film_description, f.film_releaseDate, f.film_duration, " +
                    "m.id AS mpa_id, m.mpa_status, " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id ";

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.id, f.film_name, f.film_description, f.film_releaseDate, f.film_duration, " +
                    "m.id AS mpa_id, m.mpa_status " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "WHERE f.id = ?";
    private static final String FIND_GENRES_BY_ID_QUERY =
            "SELECT fg.genre_id, g.genre_name " +
                    "FROM film_genres fg " +
                    "LEFT JOIN genres g ON fg.genre_id = g.id " +
                    "WHERE fg.film_id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(film_name, film_description, film_releaseDate, film_duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_QUERY_INTO_FILM_GENRES = "INSERT INTO film_genres(film_id, genre_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String INSERT_QUERY_LIKES = "INSERT INTO likes(film_like_id, user_like_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_like_id = ? AND user_like_id = ?";

    private static final String UPDATE_QUERY = "UPDATE films SET film_name = ?, film_description = ?, film_releaseDate = ?, " +
            "film_duration = ?, mpa_id = ? WHERE id = ?";

    private static final String LIKES_COUNT_QUERY =
            "SELECT COUNT(user_like_id) AS like_count " +
                    "FROM likes " +
                    "WHERE film_like_id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(FilmDbStorage.class);

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film : films) {
            List<Genres> genres = jdbc.query(FIND_GENRES_BY_ID_QUERY, (rs, rowNum) -> {
                Genres genre = new Genres();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                return genre;
            }, film.getId());
            film.setGenres(genres);
        }
        return films;
    }

    public Film create(NewFilmRequest filmRequest) {
        Optional<Mpa> mpa = findMpaById(filmRequest.getMpa().getId());
        if (mpa.isEmpty()) {
            LOGGER.error("ERROR нету mpa с id - " + filmRequest.getMpa().getId());
            throw new ValidationException("ERROR нету mpa с id - " + filmRequest.getMpa().getId());
        }
        long id = insert(
                INSERT_QUERY,
                filmRequest.getName(),
                filmRequest.getDescription(),
                filmRequest.getReleaseDate(),
                filmRequest.getDuration(),
                filmRequest.getMpa().getId()
        );
        Film film = new Film();
        film.setId(id);
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());

        if (filmRequest.getGenres() != null) {
            Set<GenreRequest> genresSet = new HashSet<>(filmRequest.getGenres());
            List<Genres> genresList = new ArrayList<>();
            for (GenreRequest genre : genresSet) {
                Optional<Genres> genreOpt = findGenresById(genre.getId());
                if (genreOpt.isPresent()) {
                    Genres genres = genreOpt.get();
                    insert(INSERT_QUERY_INTO_FILM_GENRES, film.getId(), genres.getId());
                    genresList.add(genres);

                } else {
                    LOGGER.error("Error нету жанра с id - " + genre.getId());
                    throw new ValidationException("Error нету жанра с id - " + genre.getId());
                }
            }
            film.setGenres(genresList.stream().sorted(Comparator.comparingLong(Genres::getId)).toList());
        }
        film.setMpa(mpa.get());
        return film;
    }

    public Optional<Film> findById(long id) {
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, id);
        if (filmOpt.isPresent()) {
            Film film = filmOpt.get();
            List<Genres> genres = jdbc.query(FIND_GENRES_BY_ID_QUERY, (rs, rowNum) -> {
                Genres genre = new Genres();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                return genre;
            }, film.getId());
            film.setGenres(genres);
            filmOpt = Optional.of(film);
        }
        return filmOpt;
    }

    public Film update(NewFilmRequest newFilmRequest) {
        update(
                UPDATE_QUERY,
                newFilmRequest.getName(),
                newFilmRequest.getDescription(),
                newFilmRequest.getReleaseDate(),
                newFilmRequest.getDuration(),
                newFilmRequest.getMpa().getId(),
                newFilmRequest.getId()
        );
        Film film = new Film();
        LOGGER.info(newFilmRequest.toString());
        jdbc.update(DELETE_GENRES_QUERY, newFilmRequest.getId());
        LOGGER.info(newFilmRequest.toString());
        if (newFilmRequest.getGenres() != null) {
            Set<GenreRequest> genresSet = new HashSet<>(newFilmRequest.getGenres());
            List<Genres> genresList = new ArrayList<>();
            for (GenreRequest genre : genresSet) {
                Optional<Genres> genreOpt = findGenresById(genre.getId());
                if (genreOpt.isPresent()) {
                    Genres genres = genreOpt.get();
                    insert(INSERT_QUERY_INTO_FILM_GENRES, film.getId(), genres.getId());
                    genresList.add(genres);

                } else {
                    LOGGER.error("Error нету жанра с id -" + genre.getId());
                    throw new ValidationException("Error нету жанра с id - " + genre.getId());
                }
            }
            film.setGenres(genresList.stream().sorted(Comparator.comparingLong(Genres::getId)).toList());
        }
        film.setId(newFilmRequest.getId());
        film.setName(newFilmRequest.getName());
        film.setDescription(newFilmRequest.getDescription());
        film.setReleaseDate(newFilmRequest.getReleaseDate());
        film.setDuration(newFilmRequest.getDuration());
        Optional<Mpa> mpa = findMpaById(newFilmRequest.getMpa().getId());
        mpa.ifPresent(film::setMpa);
        return film;
    }

    public void addLike(long filmId, long userId) {
        insert(INSERT_QUERY_LIKES, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public Optional<Genres> findGenresById(Long genreId) {
        String query = "SELECT id, genre_name FROM genres WHERE id = ?";
        try {
            Genres genre = jdbc.queryForObject(query, (rs, rowNum) -> {
                Genres g = new Genres();
                g.setId(rs.getLong("id"));
                g.setName(rs.getString("genre_name"));
                return g;
            }, genreId);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Mpa> findMpaById(Long mpaId) {
        final String FIND_MPA_BY_ID = "SELECT id, mpa_status FROM mpa WHERE id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(FIND_MPA_BY_ID, (rs, rowNum) -> {
                Mpa m = new Mpa();
                m.setId(rs.getLong("id"));
                m.setName(rs.getString("mpa_status"));
                return m;
            }, mpaId);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Collection<Mpa> getAllMpa() {
        final String FIND_ALL_MPA = "SELECT id, mpa_status FROM mpa";
        return jdbc.query(FIND_ALL_MPA, (rs, rowNum) -> {
            Mpa m = new Mpa();
            m.setId(rs.getLong("id"));
            m.setName(rs.getString("mpa_status"));
            return m;
        });
    }

    public Collection<Genres> getAllGenres() {
        final String FIND_ALL_GENRE = "SELECT id, genre_name FROM genres";
        return jdbc.query(FIND_ALL_GENRE, (rs, rowNum) -> {
            Genres g = new Genres();
            g.setId(rs.getLong("id"));
            g.setName(rs.getString("genre_name"));
            return g;
        });
    }

    public Long findLikeCountForFilm(Long filmId) {
        return jdbc.queryForObject(LIKES_COUNT_QUERY, (rs, rowNum) -> rs.getLong("like_count"), filmId);
    }
}
