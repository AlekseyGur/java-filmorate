package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.List;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    Genre addGenre(Genre genre);

    void removeGenre(Long id);

    Genre getGenre(Long id);

    List<Genre> updateFilmGenres(Long filmId, List<Long> genres);

    List<Genre> findAll();

    void deleteFilmGenreAll(Long filmId);

    List<Genre> findAllByFilmId(Long filmId);

    HashMap<Long, List<Genre>> findAllByFilmIds(List<Long> filmsIds);
}