package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmService filmService;
    private final ToolsDb toolsDb;

    public List<User> findAll() {
        return convertUserDtoToUser(userStorage.findAll());
    }

    public User getUser(Long id) {
        checkUserNotNullAndIdExistOrThrowIfNot(id);
        return convertUserDtoToUser(userStorage.getUser(id).orElse(null));
    }

    public User addUser(User user) {
        return convertUserDtoToUser(userStorage.addUser(user).orElse(null));
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
        return convertUserDtoToUser(userStorage.getFriends(userId));
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        checkUsersExistOrThrowIfNot(userId, otherUserId);
        return convertUserDtoToUser(userStorage.getCommonFriends(userId, otherUserId));
    }

    public List<Film> getRecommendations(Long userId) {
        return filmService.getRecommendedFilms(userId);
    }

    public User updateUser(User user) {
        checkUserNotNullAndIdExistOrThrowIfNot(user);
        return convertUserDtoToUser(userStorage.updateUser(user).orElse(null));
    }

    public List<User> convertUserDtoToUser(List<UserDto> usersDto) {
        return usersDto.stream().map(this::convertUserDtoToUser).collect(Collectors.toList());
    }

    public User convertUserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        user.setLogin(userDto.getLogin());
        return user;
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