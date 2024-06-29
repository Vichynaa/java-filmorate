package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserInterface {
    public User create(User user);

    public User update(User newUser);

    public Collection<User> findAll();

    public List<User> getSameFriends(Long firstId, Long secondId);

    public List<User> getFriends(Long id);

    public void deleteFriend(Long firstId, Long secondId);

    public void addFriend(Long firstId, Long secondId);
}
