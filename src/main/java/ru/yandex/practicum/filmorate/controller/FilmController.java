package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DbFilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilmController.class);
    private final DbFilmService filmService;

    public FilmController(DbFilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> findAll() {
        LOGGER.info("Get /films");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody NewFilmRequest film) {
        LOGGER.info("Post /films");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody NewFilmRequest newFilm) {
        LOGGER.info("Put /films");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        LOGGER.info(String.format("Put /films/%d/like/%d", id, userId));
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        LOGGER.info(String.format("Delete /films/%d/like/%d", id, userId));
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findList(@RequestParam Optional<Integer> count) {
        LOGGER.info("Get /films/popular");
        return filmService.findList(count);
    }

    @GetMapping("/{filmId}")
    public Film findFilmById(@PathVariable Long filmId) {
        LOGGER.info(String.format("Get /films/%d", filmId));
        return filmService.findFilmById(filmId);
    }
}
