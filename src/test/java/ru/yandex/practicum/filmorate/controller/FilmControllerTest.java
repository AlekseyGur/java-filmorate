package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@SpringBootTest
@Transactional
public class FilmControllerTest {
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private FilmController filmController;
    @Autowired
    private FilmService filmService;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private FilmRowMapper filmMapper;

    @Autowired
    private MpaStorage mpaStorage;

    @Autowired
    private GenreController genreController;
    @Autowired
    private GenreService genreService;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private GenreRowMapper genreMapper;

    @Test
    void testCreateNormal() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        filmController.create(film);

        Collection<Film> films = filmController.findAll();
        assertTrue(films.size() == 1, "Фильм должен добавиться");
    }

    @Test
    void testUpdateNormal() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        Film createdFilm = filmController.create(film);

        Film film2 = new Film();
        film2.setName("New name");
        film2.setId(createdFilm.getId());
        film2.setDescription("Small descr");
        film2.setDuration(55);
        film2.setMpa(mpaStorage.getMpa(1L));
        film2.setReleaseDate(LocalDate.parse("1995-04-21").toString());
        filmController.update(film2);

        Collection<Film> films = filmController.findAll();
        Film firstFilm = films.iterator().next();
        assertTrue(films.size() == 1, "Фильм должен добавиться");
        assertTrue(firstFilm.getName() == "New name", "Фильм должен изменить название");
    }

    @Test
    void testCreateValidErrorBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> filmController.create(film), "Исключение пустого имени");
    }

    @Test
    void testCreateValidErrorLongDescr() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("a".repeat(Film.MAX_DESCRIPTION_LEN));
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("a".repeat(Film.MAX_DESCRIPTION_LEN + 1));
        film2.setDuration(55);
        film2.setMpa(mpaStorage.getMpa(1L));
        film2.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> filmController.create(film2), "Исключение длинного имени");
    }

    @Test
    void testCreateValidErrorOldReleaseDate() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Descr");
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("Descr");
        film2.setDuration(55);
        film2.setMpa(mpaStorage.getMpa(1L));
        film2.setReleaseDate(LocalDate.parse(Film.MIN_RELEASE_DATE).minusYears(5).toString());

        assertThrows(ValidationException.class, () -> filmController.create(film2),
                "Дата релиза — не раньше предельного!");
    }

    @Test
    void testCreateValidErrorDuration() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Descr");
        film.setDuration(55);
        film.setMpa(mpaStorage.getMpa(1L));
        film.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("Descr");
        film2.setDuration(-1);
        film2.setMpa(mpaStorage.getMpa(1L));
        film2.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertThrows(ValidationException.class, () -> filmController.create(film2),
                "Продолжительность должна быть положительной!");

        Film film3 = new Film();
        film3.setName("Any name");
        film3.setDescription("Descr");
        film3.setDuration(0);
        film3.setMpa(mpaStorage.getMpa(1L));
        film3.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertThrows(ValidationException.class, () -> filmController.create(film3),
                "Продолжительность должна быть больше нуля!");
    }
}
