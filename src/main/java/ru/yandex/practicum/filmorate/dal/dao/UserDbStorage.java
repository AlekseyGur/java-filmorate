package ru.yandex.practicum.filmorate.dal.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Component
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String USER_ADD = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?);";
    private static final String USER_GET_BY_ID = "SELECT * FROM users WHERE id = ?;";
    private static final String USER_GET_ALL = "SELECT * FROM users;";
    private static final String USER_UPDATE = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;";
    private static final String USER_DELETE = "DELETE FROM users WHERE id = ? LIMIT 1;";
    private static final String FRIEND_ADD = "INSERT INTO friends(sender_id, receiver_id) VALUES (?, ?);";
    private static final String FRIEND_DELETE = "DELETE FROM friends WHERE sender_id = ? AND receiver_id = ? LIMIT 1;";
    private static final String FRIEND_GET_ALL = "SELECT u.* FROM users u JOIN friends f ON u.id = f.receiver_id WHERE f.sender_id = ?;";
    private static final String FRIEND_COMMON = "SELECT * FROM users WHERE id IN (SELECT f1.receiver_id FROM friends f1 JOIN friends f2 ON f1.receiver_id = f2.receiver_id WHERE f1.sender_id = ? AND f2.sender_id = ?);";
    private static final String USERS_LIKED_FILM = " SELECT u.id FROM users u JOIN films_likes fl ON u.id = fl.user_id WHERE fl.film_id = ?;";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        long id = insert(USER_ADD,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        return getUserImpl(id).orElse(null);
    }

    @Override
    public User getUser(Long id) {
        return getUserImpl(id).orElse(null);
    }

    @Override
    public User updateUser(User user) {
        update(USER_UPDATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return getUserImpl(user.getId()).orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        delete(USER_DELETE, id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        insert(FRIEND_ADD, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        update(FRIEND_DELETE, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return findMany(FRIEND_GET_ALL, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return findMany(FRIEND_COMMON, userId, otherId);
    }

    @Override
    public List<User> findAll() {
        return findMany(USER_GET_ALL);
    }

    public List<Long> getFilmLikesUsersIds(Long filmId) {
        return findManyIds(USERS_LIKED_FILM, filmId);
    }

    private Optional<User> getUserImpl(Long id) {
        return findOne(USER_GET_BY_ID, id);
    }
}