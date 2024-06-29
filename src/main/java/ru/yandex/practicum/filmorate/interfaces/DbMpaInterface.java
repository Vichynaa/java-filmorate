package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface DbMpaInterface {
    public Collection<Mpa> findAll();
    public Mpa findMpaById(Long mpaId);
}
