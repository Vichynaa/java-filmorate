package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        LOGGER.info("Get /users");
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        LOGGER.info("Post /users");
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        LOGGER.info("Put /users");
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        LOGGER.info(String.format("Put /users/{%d}/friends/{%d}", id, friendId));
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        LOGGER.info(String.format("Delete /users/{%d}/friends/{%d}", id, friendId));
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        LOGGER.info(String.format("Get /users/{%d}/friends", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getSameFriends(@PathVariable Long id, @PathVariable Long otherId) {
        LOGGER.info(String.format("Get /users/{%d}/friends/common/{%d}", id, otherId));
        return userService.getSameFriends(id, otherId);
    }
}
