package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DbMpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MpaController.class);
    private final DbMpaService mpaService;
    public MpaController(DbMpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        LOGGER.info("Get /mpa");
        return mpaService.findAll();
    }

    @GetMapping("/{mpaId}")
    public Mpa findMpaById(@PathVariable Long mpaId) {
        LOGGER.info(String.format("Get /mpa/%s", mpaId));
        return mpaService.findMpaById(mpaId);
    }

}
