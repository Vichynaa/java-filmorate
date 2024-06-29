package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;

public interface DbGenresInterface {
    public Collection<Genres> findAll();
    public Genres findNameById(Long genresId);
}
