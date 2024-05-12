package ru.yandex.practicum.filmorate.controller;

import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET request /films");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("POST request /films");
        if (film.getName().isBlank()) {
            log.error("Error при валидации, название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        validation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("PUT request /films");
        if (newFilm.getId() == null) {
            log.error("Error при валидации, id должен быть указан");
            throw new ValidationException("id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            validation(newFilm);
            setFields(newFilm);
            return films.get(newFilm.getId());
        }
        log.error("Error при валидации, Пост с id = " + newFilm.getId() + " не найден");
        throw new ValidationException("Пост с id = " + newFilm.getId() + " не найден");
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

    private void validation(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Error при валидации, Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Error при валидации, Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Error при валидации, Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
