package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    // Добавляет нового пользователя в хранилище
    Optional<UserDto> addUser(User user);

    // Получает пользователя по его уникальному идентификатору
    Optional<UserDto> getUser(Long id);

    // Обновляет информацию о пользователе
    Optional<UserDto> updateUser(User user);

    // Удаляет пользователя по его уникальному идентификатору
    void deleteUser(Long id);

    // Добавляет одного пользователя в друзья другого
    void addFriend(Long userId, Long friendId);

    // Удаляет одного пользователя из друзей другого
    void removeFriend(Long userId, Long friendId);

    // Получает список друзей указанного пользователя
    List<UserDto> getFriends(Long userId);

    // Получает список общих друзей двух пользователей
    List<UserDto> getCommonFriends(Long userId, Long otherId);

    // Все пользователи
    List<UserDto> findAll();
}