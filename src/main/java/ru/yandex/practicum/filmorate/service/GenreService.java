package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final ToolsDb toolsDb;

    public Genre get(Long id) {
        Genre genre = genreStorage.getGenre(id);
        if (genre == null) {
            throw new NotFoundException("Неизвестный жанр с id: " + id);
        }
        return genre;
    }

    public HashMap<Long, List<Genre>> getGenresForFilm(List<Long> ids) {
        for (Long id : ids) {
            checkGenreIdNotNullOrThrowIfNot(id);
        }
        return genreStorage.findAllByFilmIds(ids);
    }

    public Genre create(Genre genre) {
        return genreStorage.addGenre(genre);
    }

    public void remove(Long id) {
        checkGenreIdNotNullOrThrowIfNot(id);
        genreStorage.removeGenre(id);
    }

    public List<Genre> updateFilmGenres(Long filmId, List<Long> genresIds) {
        filterGenresThrowIfNotExist(genresIds);
        return genreStorage.updateFilmGenres(filmId, genresIds);
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public void deleteFilmGenreAll(Long filmId) {
        genreStorage.deleteFilmGenreAll(filmId);
    }

    private void checkGenreIdNotNullOrThrowIfNot(Long genreId) {
        if (genreId == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
    }

    public List<Long> filterGenresThrowIfNotExist(List<Long> genresIds) {
        List<Long> genresIdsFiltered = null;

        if (genresIds != null) {
            genresIdsFiltered = genresIds.stream().filter(x -> x != null).distinct().toList();

            if (!genresIdsFiltered.isEmpty() && !toolsDb.unsafeCheckTableContainsIds("genre", genresIdsFiltered)) {
                throw new NotFoundException(
                        "В списке жанров найден неизвестный id жанра: " + genresIdsFiltered.toString());
            }
        }

        return genresIdsFiltered;
    }
}
