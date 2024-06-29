package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.interfaces.DbGenresInterface;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.Optional;

@Service
public class DbGenresService implements DbGenresInterface {
    private final FilmDbStorage filmDbStorage;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbGenresService.class);

    public DbGenresService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Collection<Genres> findAll() {
        return filmDbStorage.getAllGenres();
    }

    @Override
    public Genres findNameById(Long genresId) {
        Optional<Genres> genres = filmDbStorage.findGenresById(genresId);
        if (genres.isEmpty()) {
            LOGGER.error("Error жанра с id - " + genresId + " нет");
            throw new NotFoundException("Error жанра с id - " + genresId + " нет");
        }
        return genres.get();
    }
}
