package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT id, user_email, user_login, user_name, user_birthday FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users(user_email, user_login, user_name, user_birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_FRIENDS = "INSERT INTO friends(from_user_id, to_user_id, status)" +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?," +
            " user_birthday = ? WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_BY_USER_ID = "SELECT to_user_id, status FROM friends WHERE from_user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(long userId) {
        Optional<User> userOpt = findOne(FIND_BY_ID_QUERY, userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Map<String, Object>> results = jdbc.queryForList(FIND_FRIENDS_BY_USER_ID, userId);
            Map<Long, FriendStatus> friends = new HashMap<>();
            for (Map<String, Object> row : results) {
                Long to_user_id = ((Number) row.get("to_user_id")).longValue();
                FriendStatus status = FriendStatus.valueOf(row.get("status").toString());
                friends.put(to_user_id, status);
            }
            user.setFromUserRequest(friends);
            userOpt = Optional.of(user);
        }
        return userOpt;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        update(
                UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        String DELETE_FRIENDS_QUERY = "DELETE FROM friends WHERE from_user_id = ?";
        jdbc.update(DELETE_FRIENDS_QUERY, newUser.getId());

        for (Map.Entry<Long, FriendStatus> entry : newUser.getFromUserRequest().entrySet()) {
            Long toUserId = entry.getKey();
            FriendStatus status = entry.getValue();
            jdbc.update(INSERT_FRIENDS, newUser.getId(), toUserId, status.name());
        }

        return newUser;
    }
}
