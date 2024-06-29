package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DbFilmInterface {
    public Film create(NewFilmRequest film);

    public Film update(NewFilmRequest NewFilmRequest);

    public Collection<Film> findAll();
    public Film findFilmById(Long filmId);

    public List<Film> findList(Optional<Integer> count);

    public void removeLike(Long filmId, Long userId);

    public void like(Long filmId, Long userId);


}
