package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.DbGenresService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenresController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenresController.class);
    private final DbGenresService genresService;

    public GenresController(DbGenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    public Collection<Genres> findAll() {
        LOGGER.info("Get /genres");
        return genresService.findAll();
    }

    @GetMapping("/{genresId}")
    public Genres findMpaById(@PathVariable Long genresId) {
        LOGGER.info(String.format("Get /genres/%s", genresId));
        return genresService.findNameById(genresId);
    }
}
