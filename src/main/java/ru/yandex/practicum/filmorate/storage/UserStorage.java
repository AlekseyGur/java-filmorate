package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    // Добавляет нового пользователя в хранилище
    User addUser(User user);

    // Получает пользователя по его уникальному идентификатору
    User getUser(Long id);

    // Обновляет информацию о пользователе
    User updateUser(User user);

    // Удаляет пользователя по его уникальному идентификатору
    void deleteUser(Long id);

    // Добавляет одного пользователя в друзья другого
    void addFriend(Long userId, Long friendId);

    // Удаляет одного пользователя из друзей другого
    void removeFriend(Long userId, Long friendId);

    // Получает список друзей указанного пользователя
    List<User> getFriends(Long userId);

    // Получает список общих друзей двух пользователей
    List<User> getCommonFriends(Long userId, Long otherId);

    // Все пользователи
    List<User> findAll();
}