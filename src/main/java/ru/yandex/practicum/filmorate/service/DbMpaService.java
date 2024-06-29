package ru.yandex.practicum.filmorate.service;


import exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.interfaces.DbMpaInterface;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Service
public class DbMpaService implements DbMpaInterface {
    private final FilmDbStorage filmDbStorage;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMpaService.class);
    public DbMpaService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Collection<Mpa> findAll() {
        return filmDbStorage.getAllMpa();
    }

    @Override
    public Mpa findMpaById(Long mpaId) {
        Optional<Mpa> mpa = filmDbStorage.findMpaById(mpaId);
        if (mpa.isEmpty()) {
            LOGGER.error("Error mpa с id - " + mpaId + " нет");
            throw new NotFoundException("Error mpa с id - " + mpaId + " нет");
        }
        return mpa.get();
    }
}
