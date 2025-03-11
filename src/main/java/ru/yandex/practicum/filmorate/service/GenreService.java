package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre get(Long id) {
        return genreStorage.getGenre(id);
    }

    public Genre create(Genre genre) {
        return genreStorage.addGenre(genre);
    }

    public void remove(Long id) {
        genreStorage.removeGenre(id);
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }
}
