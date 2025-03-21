package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Component
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final ToolsDb toolsDb;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(Long id) {
        checkUserNotNullAndIdExistOrThrowIfNot(id);
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        checkUsersExistOrThrowIfNot(userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        checkUsersExistOrThrowIfNot(userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        checkUserNotNullAndIdExistOrThrowIfNot(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        checkUsersExistOrThrowIfNot(userId, otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public User updateUser(User user) {
        checkUserNotNullAndIdExistOrThrowIfNot(user);
        return userStorage.updateUser(user);
    }

    public void checkUserNotNullAndIdExistOrThrowIfNot(Long id) {
        if (!toolsDb.unsafeCheckTableContainsId("users", id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public void checkUserNotNullAndIdExistOrThrowIfNot(User user) {
        if (user == null) {
            throw new ConditionsNotMetException("Необходимо передать данные фильма");
        }
        checkUserNotNullAndIdExistOrThrowIfNot(user.getId());
    }

    private void checkUsersExistOrThrowIfNot(Long userId, Long otherId) {
        if (!toolsDb.unsafeCheckTableContainsId("users", userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!toolsDb.unsafeCheckTableContainsId("users", otherId)) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
    }
}