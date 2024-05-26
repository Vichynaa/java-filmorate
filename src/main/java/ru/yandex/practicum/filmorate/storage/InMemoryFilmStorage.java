package ru.yandex.practicum.filmorate.storage;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        if (film.getName().isBlank()) {
            LOGGER.error("Error при валидации, название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        validation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            LOGGER.error("Error при валидации, id должен быть указан");
            throw new ValidationException("id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            validation(newFilm);
            setFields(newFilm);
            return films.get(newFilm.getId());
        }
        LOGGER.error("Error при валидации, Пост с id = " + newFilm.getId() + " не найден");
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }



    private void validation(Film film) {
        if (film.getDescription().length() > 200) {
            LOGGER.error("Error при валидации, Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            LOGGER.error("Error при валидации, Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            LOGGER.error("Error при валидации, Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void setFields(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
    }

    public Map<Long, Film> getFilms() {
        return films;
    }
}
