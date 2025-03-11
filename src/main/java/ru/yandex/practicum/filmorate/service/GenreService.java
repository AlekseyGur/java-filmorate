package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Service
public class GenreService {

    private final GenreStorage GenreStorage;

    public GenreService(GenreStorage GenreStorage) {
        this.GenreStorage = GenreStorage;
    }
    
    public Genre get(Long id) {
        return GenreStorage.getGenre(id);
    }
    
    public Genre create(Genre Genre) {
        return GenreStorage.addGenre(Genre);
    }

    public void remove(Long id) {
        GenreStorage.removeGenre(id);
    }

    public List<Genre> findAll() {
        return GenreStorage.findAll();
    }
}
