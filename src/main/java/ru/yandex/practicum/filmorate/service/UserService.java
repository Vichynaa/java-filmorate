package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public String addFriend(Long firstId, Long secondId) {
        if (!inMemoryUserStorage.getUsers().containsKey(firstId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(secondId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstFriend = inMemoryUserStorage.getUsers().get(firstId);
        User secondFriend = inMemoryUserStorage.getUsers().get(secondId);
        Set<Long> newFirstFriends = firstFriend.getFriends();
        Set<Long> newSecondFriends = secondFriend.getFriends();
        boolean checkAddFriend = newSecondFriends.add(firstFriend.getId());
        newFirstFriends.add(secondFriend.getId());
        if (!checkAddFriend) {
            LOGGER.debug(String.format("Debug пользователи с id - %d и %d, уже друзья", firstId, secondId));
            throw new ValidationException(String.format("Пользователи с id - %d и %d, уже друзья", firstId, secondId));
        }
        firstFriend.setFriends(newFirstFriends);
        secondFriend.setFriends(newSecondFriends);
        LOGGER.info(String.format("Info пользователи с id - %d и %d, стали друзьями", firstId, secondId));
        return String.format("Пользователи с id - %d и %d, стали друзьями", firstId, secondId);
    }

    public String deleteFriend(Long firstId, Long secondId) {
        if (!inMemoryUserStorage.getUsers().containsKey(firstId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(secondId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstFriend = inMemoryUserStorage.getUsers().get(firstId);
        User secondFriend = inMemoryUserStorage.getUsers().get(secondId);
        Set<Long> newFirstFriends = firstFriend.getFriends();
        Set<Long> newSecondFriends = secondFriend.getFriends();
        boolean checkFriend = newFirstFriends.remove(secondFriend.getId());
        newSecondFriends.remove(firstFriend.getId());
        if (!checkFriend) {
            LOGGER.debug(String.format("Debug пользователи с id - %d и %d, не являются друзьями", firstId, secondId));
            throw new ValidationException(String.format(
                    "Пользователи с id - %d и %d, не являются друзьями", firstId, secondId));
        }
        firstFriend.setFriends(newFirstFriends);
        secondFriend.setFriends(newSecondFriends);
        LOGGER.info(String.format("Info пользователи с id - %d и %d, перестали быть друзьями", firstId, secondId));
        return String.format("Пользователи с id - %d и %d, перестали быть друзьями", firstId, secondId);
    }

    public List<User> getFriends(Long id) {
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", id));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", id));
        }
        return inMemoryUserStorage.getUsers().get(id).getFriends().stream()
                .map(inMemoryUserStorage.getUsers()::get)
                .toList();
    }

    public List<User> getSameFriends(Long firstId, Long secondId) {
        if (!inMemoryUserStorage.getUsers().containsKey(firstId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (!inMemoryUserStorage.getUsers().containsKey(secondId)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstUser = inMemoryUserStorage.getUsers().get(firstId);
        User secondUser = inMemoryUserStorage.getUsers().get(secondId);
        List<Long> sameFriends = firstUser.getFriends().stream()
                .filter(secondUser.getFriends()::contains)
                .toList();
        return sameFriends.stream()
                .map(inMemoryUserStorage.getUsers()::get)
                .toList();
    }

}
