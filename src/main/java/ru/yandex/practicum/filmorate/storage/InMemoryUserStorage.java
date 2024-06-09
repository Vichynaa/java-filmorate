package ru.yandex.practicum.filmorate.storage;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getEmail() == null || !(user.getEmail().contains("@"))) {
            LOGGER.info("POST request /users");
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

    @Override
    public User update(User newUser) {
        if (newUser == null) {
            LOGGER.error("Error не передан пользователь");
            throw new ValidationException("Не передан пользователь");
        }
        if (newUser.getId() == null) {
            LOGGER.error("Error при валидации, id должен быть указан");
            throw new ValidationException("id должен быть указан");
        }
        if (!(users.containsKey(newUser.getId()))) {
            LOGGER.error("Error при валидации, такого id не существует");
            throw new NotFoundException("Такого id не существует");
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

    public Map<Long, User> getUsers() {
        return users;
    }
}