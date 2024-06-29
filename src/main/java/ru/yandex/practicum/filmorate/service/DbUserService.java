package ru.yandex.practicum.filmorate.service;

import exception.NotFoundException;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.interfaces.DbUserInterface;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Service
public class DbUserService implements DbUserInterface {
    private final UserDbStorage userDbStorage;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUserService.class);

    public DbUserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Override
    public UserDto create(User request) {
        if (request.getEmail() == null || !(request.getEmail().contains("@"))) {
            LOGGER.info("POST request /users");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (request.getLogin() == null || request.getLogin().contains(" ")) {
            LOGGER.error("Error при валидации, логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (request.getName() == null) {
            request.setName(request.getLogin());
        }
        if (request.getBirthday().isAfter(LocalDate.now())) {
            LOGGER.error("Error при валидации, дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        User user = userDbStorage.create(request);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(User request) {
        if (request == null) {
            LOGGER.error("Error не передан пользователь");
            throw new ValidationException("Не передан пользователь");
        }
        if (request.getId() == null) {
            LOGGER.error("Error при валидации, id должен быть указан");
            throw new ValidationException("id должен быть указан");
        }
        if (userDbStorage.findById(request.getId()).isEmpty()) {
            LOGGER.error("Error при валидации, такого id не существует");
            throw new NotFoundException("Такого id не существует");
        }
        if (request.getEmail() != null && !(request.getEmail().contains("@"))) {
            LOGGER.error("Error при валидации, электронная почта должна содержать символ @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (request.getLogin() != null && request.getLogin().contains(" ")) {
            LOGGER.error("Error при валидации, логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (request.getBirthday() != null && request.getBirthday().isAfter(LocalDate.now())) {
            LOGGER.error("Error при валидации, дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        User oldUser = userDbStorage.findById(request.getId()).get();
        oldUser.setBirthday(request.getBirthday());
        oldUser.setEmail(request.getEmail());
        oldUser.setLogin(request.getLogin());
        if (request.getName() != null) {
            oldUser.setName(request.getName());
        } else {
            oldUser.setName(oldUser.getLogin());
        }
        userDbStorage.update(oldUser);
        return UserMapper.mapToUserDto(oldUser);
    }

    @Override
    public Collection<UserDto> findAll() {
        return userDbStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getSameFriends(Long firstId, Long secondId) {
        if (userDbStorage.findById(firstId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (userDbStorage.findById(secondId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstUser = userDbStorage.findById(firstId).get();
        User secondUser = userDbStorage.findById(secondId).get();
        List<Long> firstUserFriends = firstUser.getFromUserRequest().keySet().stream()
                .toList();
        List<Long> secondUserFriends = secondUser.getFromUserRequest().keySet().stream()
                .toList();
        List<Long> sameFriends = firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .toList();

        return sameFriends.stream()
                .map(userId -> userDbStorage.findById(userId).orElse(null))
                .filter(Objects::nonNull)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getFriends(Long id) {
        if (userDbStorage.findById(id).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", id));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", id));
        }
        return userDbStorage.findById(id).get().getFromUserRequest().keySet().stream()
                .map(userId -> userDbStorage.findById(userId).orElse(null))
                .filter(Objects::nonNull)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public void deleteFriend(Long firstId, Long secondId) {
        if (userDbStorage.findById(firstId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (userDbStorage.findById(secondId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstFriend = userDbStorage.findById(firstId).get();
        User secondFriend = userDbStorage.findById(secondId).get();
        Map<Long, FriendStatus> newFirstFriends = firstFriend.getFromUserRequest();
        newFirstFriends.remove(secondFriend.getId());
        firstFriend.setFromUserRequest(newFirstFriends);
        userDbStorage.update(firstFriend);
        LOGGER.info(String.format("Info пользователи с id - %d и %d, перестали быть друзьями", firstId, secondId));
    }

    @Override
    public void addFriend(Long firstId, Long secondId) {
        if (userDbStorage.findById(firstId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", firstId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", firstId));
        }
        if (userDbStorage.findById(secondId).isEmpty()) {
            LOGGER.error(String.format("Error пользватель с id - %d, не найден", secondId));
            throw new NotFoundException(String.format("Пользватель с id - %d, не найден", secondId));
        }
        User firstFriend = userDbStorage.findById(firstId).get();
        User secondFriend = userDbStorage.findById(secondId).get();
        Map<Long, FriendStatus> firstUserFriends = firstFriend.getFromUserRequest();
        Map<Long, FriendStatus> secondUserFriends = secondFriend.getFromUserRequest();

        if (!firstUserFriends.containsKey(secondId)) {
            if (!secondUserFriends.containsKey(firstId)) {
                firstUserFriends.put(secondId, FriendStatus.UNCONFIRMED);
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
        firstFriend.setFromUserRequest(firstUserFriends);
        secondFriend.setFromUserRequest(secondUserFriends);
        userDbStorage.update(firstFriend);
        userDbStorage.update(secondFriend);
    }

}
