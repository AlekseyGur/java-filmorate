package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }
}