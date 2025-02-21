package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.Validate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        Validate.film(film);
        return filmService.create(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        Validate.film(film);
        return filmService.update(film);
    }
}
