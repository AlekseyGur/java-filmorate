package ru.yandex.practicum.filmorate.exception;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public class Validate {

    public static void film(@Valid Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().strip().length() > Film.MAX_DESCRIPTION_LEN) {
            throw new ValidationException("Максимальная длина описания — " + Film.MAX_DESCRIPTION_LEN + " символов");
        }
        if (film.getReleaseDate() != null
                && LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.parse(Film.MIN_RELEASE_DATE))) {
            throw new ValidationException("Дата релиза — не раньше " + Film.MIN_RELEASE_DATE);
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public static void user(@Valid User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")
                || !isValidEmail(user.getEmail())) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            // Имя может быть пустым — в таком случае будет использован
            user.setName(user.getLogin());
        }

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
