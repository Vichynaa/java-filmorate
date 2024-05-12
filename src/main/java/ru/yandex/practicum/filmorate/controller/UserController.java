package ru.yandex.practicum.filmorate.controller;

import exception.ValidationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        LOGGER.info("GET request /users");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        LOGGER.info("POST request /users");
        if (user.getEmail() == null || !(user.getEmail().contains("@"))) {
            LOGGER.error("Error при валидации, электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            LOGGER.error("Error при валидации, логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            LOGGER.error("Error при валидации, дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        LOGGER.info("PUT request /users");
        if (newUser.getId() == null) {
            LOGGER.error("Error при валидации, id должен быть указан");
            throw new ValidationException("id должен быть указан");
        }
        if (!(users.containsKey(newUser.getId()))) {
            LOGGER.error("Error при валидации, такого id не существует");
            throw new ValidationException("Такого id не существует");
        }
        User oldUser = users.get(newUser.getId());
        if (newUser.getEmail() != null && !(newUser.getEmail().contains("@"))) {
            LOGGER.error("Error при валидации, электронная почта должна содержать символ @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (newUser.getLogin() != null && newUser.getLogin().contains(" ")) {
            LOGGER.error("Error при валидации, логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            LOGGER.error("Error при валидации, дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        } else {
            oldUser.setName(oldUser.getLogin());
        }
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
