package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmInterface {
    public Film create(Film film);

    public Film update(Film newFilm);

    public Collection<Film> findAll();

    public List<Film> findList(Optional<Integer> count);

    public void removeLike(Long filmId, Long userId);

    public void like(Long filmId, Long userId);


}
