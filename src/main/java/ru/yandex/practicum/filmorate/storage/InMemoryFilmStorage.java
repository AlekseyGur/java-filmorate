package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);

        filmLikes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getDescription() == null || newFilm.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            // если фильм найден и все условия соблюдены, обновляем её содержимое
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return newFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
        filmLikes.remove(id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (inMemoryUserStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (inMemoryUserStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        filmLikes.get(filmId).remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = new ArrayList<>(films.values());
        popularFilms.sort((f1, f2) -> Integer.compare(
                filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()));

        return popularFilms.subList(0, Math.min(count, popularFilms.size()));
    }

}
