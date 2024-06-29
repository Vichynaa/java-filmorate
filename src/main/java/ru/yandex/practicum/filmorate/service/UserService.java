package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.UserInterface;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
public class UserService implements UserInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void addFriend(Long firstId, Long secondId) {
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
        Map<Long, FriendStatus> firstUserFriends = firstFriend.getFromUserRequest();
        Map<Long, FriendStatus> secondUserFriends = secondFriend.getFromUserRequest();

        if (!firstUserFriends.containsKey(secondId)) {
            if (!secondUserFriends.containsKey(firstId)) {
                firstUserFriends.put(secondId, FriendStatus.UNCONFIRMED);
                firstFriend.setFromUserRequest(firstUserFriends);
            }
            else {
                firstUserFriends.put(secondId, FriendStatus.CONFIRMED);
                secondUserFriends.put(firstId, FriendStatus.CONFIRMED);
                LOGGER.info(String.format("Info пользователи с id - %d и %d, стали друзьями", firstId, secondId));
            }
        }
        else {
            if (firstUserFriends.get(secondId) == FriendStatus.CONFIRMED) {
                LOGGER.debug(String.format("Debug пользователи с id - %d и %d, уже друзья",
                        firstId, secondId));
                throw new ValidationException(String.format("Пользователи с id - %d и %d, уже друзья",
                        firstId, secondId));
            }
            else {
                LOGGER.debug(String.format("Debug пользователь с id - %d, уже отправил запрос пользователю с id - %d",
                        firstId, secondId));
                throw new ValidationException(String.format("Debug пользователь с id - %d," +
                                " уже отправил запрос пользователю с id - %d",
                        firstId, secondId));
            }

        }
    }

    @Override
    public void deleteFriend(Long firstId, Long secondId) {
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
        Map<Long, FriendStatus> newFirstFriends = firstFriend.getFromUserRequest();
        Map<Long, FriendStatus> newSecondFriends = secondFriend.getFromUserRequest();
        if (!(newFirstFriends.containsKey(secondId) || newSecondFriends.containsKey(firstId))) {
            LOGGER.debug(String.format("Debug пользователи с id - %d и %d, не являются друзьями",
                    firstId, secondId));
            throw new ValidationException(String.format("Debug пользователи с id - %d и %d, не являются друзьями",
                    firstId, secondId));
        }
        newFirstFriends.remove(secondFriend.getId());
        newSecondFriends.remove(firstFriend.getId());
        firstFriend.setFromUserRequest(newFirstFriends);
        secondFriend.setFromUserRequest(newSecondFriends);
        LOGGER.info(String.format("Info пользователи с id - %d и %d, перестали быть друзьями", firstId, secondId));
    }

    @Override
    public List<User> getFriends(Long id) {
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", id));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", id));
        }
        return inMemoryUserStorage.getUsers().get(id).getFromUserRequest().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .map(inMemoryUserStorage.getUsers()::get)
                .toList();
    }

    @Override
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
        List<Long> firstUserFriends = firstUser.getFromUserRequest().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .toList();
        List<Long> secondUserFriends = secondUser.getFromUserRequest().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .toList();
        List<Long> sameFriends = firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .toList();

        return sameFriends.stream()
                .map(inMemoryUserStorage.getUsers()::get)
                .toList();
    }

    @Override
    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    @Override
    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

}
