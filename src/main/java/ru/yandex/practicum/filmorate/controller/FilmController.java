package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilmController.class);
    InMemoryUserStorage inMemoryUserStorage;
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;

    public FilmController(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> findAll() {
        LOGGER.info("Get /films");
        return inMemoryFilmStorage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        LOGGER.info("Post /films");
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        LOGGER.info("Put /films");
        return inMemoryFilmStorage.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public String like(@PathVariable Long id, @PathVariable Long userId) {
        LOGGER.info(String.format("Put /films/{%d}/like/{%d}", id, userId));
        return filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLike(@PathVariable Long id, @PathVariable Long userId) {
        LOGGER.info(String.format("Delete /films/{%d}/like/{%d}", id, userId));
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findList(@RequestParam Optional<Integer> count) {
        LOGGER.info("Get /films/popular");
        return filmService.findList(count);
    }
}
