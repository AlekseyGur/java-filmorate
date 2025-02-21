package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

@SpringBootTest
class FilmorateApplicationTests {

    FilmController filmController;
    FilmService filmService;
    FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void contextLoads() {
    }
}
