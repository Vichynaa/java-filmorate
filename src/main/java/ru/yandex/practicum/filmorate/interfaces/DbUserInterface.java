package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserDbInterface {
    public UserDto create(User user);

    public UserDto update(User newUser);

    public Collection<UserDto> findAll();

    public List<UserDto> getSameFriends(Long firstId, Long secondId);

    public List<UserDto> getFriends(Long id);

    public void deleteFriend(Long firstId, Long secondId);

    public void addFriend(Long firstId, Long secondId);
}
