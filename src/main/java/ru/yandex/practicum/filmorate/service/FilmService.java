package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
public class FilmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilmService.class);
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void like(Long filmId, Long userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            LOGGER.error(String.format("Error не найден фильм с id - %d", filmId));
            throw new NotFoundException(String.format("Не найден фильм с id - %d", filmId));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            LOGGER.error(String.format("Error не найден пользователь с id - %d", userId));
            throw new NotFoundException(String.format("Не найден пользователь с id - %d", userId));
        }
        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        Set<Long> newLikes = film.getLikes();
        newLikes.add(userId);
        film.setLikes(newLikes);
        LOGGER.info(String.format("Info к фильму с id - %d, поставлен лайк пользователем с id - %d",
                film.getId(), userId));
    }

    public void removeLike(Long filmId, Long userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            LOGGER.error(String.format("Error не найден фильм с id - %d", filmId));
            throw new NotFoundException(String.format("Не найден фильм с id - %d", filmId));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            LOGGER.error(String.format("Error не найден пользователь с id - %d", userId));
            throw new NotFoundException(String.format("Не найден пользователь с id - %d", userId));
        }
        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        Set<Long> newLikes = film.getLikes();
        if (!newLikes.contains(userId)) {
            LOGGER.debug(String.format("Debug пользователь с id - %d, не ставил лайк посту с id - %d", userId, filmId));
            throw new ValidationException(String.format(
                    "Пользователь с id - %d, не ставил лайк посту с id - %d", userId, filmId));
        }
        newLikes.remove(userId);
        film.setLikes(newLikes);
        LOGGER.info(String.format("Info у фильма с id - %d, удалён лайк пользователем с id - %d",
                film.getId(), userId));
    }

    public List<Film> findList(Optional<Integer> count) {
        int countToPrint = count.map(integer -> Math.min(inMemoryFilmStorage.getFilms().size(), integer)).orElse(10);
        return inMemoryFilmStorage.getFilms().values().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(countToPrint)
                .toList();
    }
}
