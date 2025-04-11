package ru.yandex.practicum.filmorate.controller;

import java.util.List;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.Validate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        likeService.removeLike(filmId, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        return filmService.search(query, by);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
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
    public Film update(@RequestBody Film film) {
        Validate.film(film);
        return filmService.update(film);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> findByDirectorSort(@PathVariable Long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.findByDirectorSort(directorId, sortBy);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> findCommonFilmsWithFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilmsWithFriend(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long filmId) {
        filmService.delete(filmId);
    }

}
