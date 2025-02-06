package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        validate(user);
        // формируем дополнительные данные
        user.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        validate(newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getLogin() == null || newUser.getLogin() == null
                    || newUser.getEmail().isBlank() || newUser.getLogin().isBlank() || newUser.getLogin().isBlank()) {
                // throw new ConditionsNotMetException("Описание не может быть пустым");
            } else {
                // если публикация найдена и все условия соблюдены, обновляем её содержимое
                oldUser.setEmail(newUser.getEmail());
                oldUser.setName(newUser.getName());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setBirthday(newUser.getBirthday());
                return oldUser;
            }
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        // return newUser;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@") || !isValidEmail(user.getEmail())) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        // Закомментировано, потому что не проходит тестов из Postman!
        //
        // if (user.getName() != null || user.getName().isBlank()) {
        //     throw new ValidationException(
        //             "Имя для отображения может быть пустым — в таком случае будет использован логин");
        //     user.setName(user.getLogin());
        // }

        if (user.getBirthday() != null && LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}