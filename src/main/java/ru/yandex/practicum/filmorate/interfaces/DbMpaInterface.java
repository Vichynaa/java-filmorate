package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaInterface {
    public Collection<Mpa> findAll();
    public Mpa findMpaById(Long mpaId);
}
