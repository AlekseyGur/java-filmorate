package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    Optional<UserDto> addUser(User user);

    Optional<UserDto> getUser(Long id);

    Optional<UserDto> updateUser(User user);

    void deleteUser(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<UserDto> getFriends(Long userId);

    List<UserDto> getCommonFriends(Long userId, Long otherId);

    List<UserDto> findAll();
}