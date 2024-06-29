package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.interfaces.DbFilmInterface;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Service
public class DbFilmService implements DbFilmInterface {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbFilmService.class);

    public DbFilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public void like(Long filmId, Long userId) {
        if (filmDbStorage.findById(filmId).isEmpty()) {
            LOGGER.error(String.format("Error не найден фильм с id - %d", filmId));
            throw new NotFoundException(String.format("Не найден фильм с id - %d", filmId));
        }
        if (userDbStorage.findById(filmId).isEmpty()) {
            LOGGER.error(String.format("Error не найден пользователь с id - %d", userId));
            throw new NotFoundException(String.format("Не найден пользователь с id - %d", userId));
        }
        filmDbStorage.addLike(filmId, userId);
        LOGGER.info(String.format("Info к фильму с id - %d, поставлен лайк пользователем с id - %d",
                filmDbStorage.findById(filmId).get().getId(), userId));
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (filmDbStorage.findById(filmId).isEmpty()) {
            LOGGER.error(String.format("Error не найден фильм с id - %d", filmId));
            throw new NotFoundException(String.format("Не найден фильм с id - %d", filmId));
        }
        if (userDbStorage.findById(userId).isEmpty()) {
            LOGGER.error(String.format("Error не найден пользователь с id - %d", userId));
            throw new NotFoundException(String.format("Не найден пользователь с id - %d", userId));
        }
        Film film = filmDbStorage.findById(filmId).get();
        Set<Long> newLikes = film.getLikes();
        if (!newLikes.contains(userId)) {
            LOGGER.debug(String.format("Debug пользователь с id - %d, не ставил лайк посту с id - %d", userId, filmId));
        }
        filmDbStorage.deleteLike(filmId, userId);
        LOGGER.info(String.format("Info у фильма с id - %d, удалён лайк пользователем с id - %d",
                film.getId(), userId));
    }

    @Override
    public List<Film> findList(Optional<Integer> count) {
        int countToPrint = count.map(integer -> Math.min(filmDbStorage.findAll().size(), integer)).orElse(10);
        return filmDbStorage.findAll().stream()
                .sorted(Comparator.comparingLong((Film film) ->  filmDbStorage.findLikeCountForFilm(film.getId())).reversed())
                .limit(countToPrint)
                .toList();
    }

    @Override
    public Film create(NewFilmRequest film) {
        if (film.getName().isEmpty()) {
            LOGGER.error("Error имя не может быть пустым");
            throw new ValidationException("Error имя не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            LOGGER.error("Error описание не должно привышать 200 символов");
            throw new ValidationException("Error описание не должно привышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            LOGGER.error("Дата не может быть раньше 28/12/1895");
            throw new ValidationException("Дата не может быть раньше 28/12/1895");
        }
        if (film.getDuration() < 0) {
            LOGGER.error("Длительность фильма не может быть пустой");
            throw new ValidationException("Длительность фильма не может быть пустой");
        }
        return filmDbStorage.create(film);
    }

    @Override
    public Film update(NewFilmRequest newFilm) {
        return filmDbStorage.update(newFilm);
    }

    @Override
    public Collection<Film> findAll() {
        return filmDbStorage.findAll();
    }

    @Override
    public Film findFilmById(Long filmId) {
        Optional<Film> filmOpt = filmDbStorage.findById(filmId);
        if (filmOpt.isEmpty()) {
            LOGGER.error("Error фильма с id - " + filmId + " нет");
            throw new NotFoundException("Error фильма с id - " + filmId + " нет");
        }
        return filmOpt.get();
    }
}
