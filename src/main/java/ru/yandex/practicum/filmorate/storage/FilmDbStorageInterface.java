package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmDbStorageInterface {
    public Collection<Film> findAll();

    public Film create(NewFilmRequest film);

    public Film update(NewFilmRequest newFilm);
}
