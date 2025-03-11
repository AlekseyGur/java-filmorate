package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    Genre addGenre(Genre genre);

    void removeGenre(Long id);

    Genre getGenre(Long id);

    List<Genre> findAll();
}