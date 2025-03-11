package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
@Component
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    @Autowired
    GenreDbStorage genreDbStorage;

    private static final String FILM_ADD = "INSERT INTO films(name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?);";
    private static final String FILM_GET_BY_ID = "SELECT * FROM films WHERE id = ?;";
    private static final String FILM_GET_ALL = "SELECT * FROM films;";
    private static final String FILM_UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id = ?;";
    private static final String FILM_DELETE = "DELETE FROM films WHERE id = ? LIMIT 1;";
    private static final String LIKES_FILMS_DESC = "SELECT f.*, COUNT(l.*) AS likes_count FROM films f LEFT JOIN films_likes l ON f.id = l.film_id GROUP BY f.id ORDER BY likes_count DESC;";
    private static final String LIKE_ADD = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
    private static final String LIKE_DELETE = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ? LIMIT 1;";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film addFilm(Film film) {
        Long mpa = null;
        ArrayList<Genre> genres = new ArrayList<>();

        if (film.getMpa() != null) {
            mpa = film.getMpa().getId();
            if (!unsafeCheckTableContainsId("mpa", mpa)) {
                throw new NotFoundException("Рейтинг с id = " + mpa + " не найден");
            }
        }

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                if (!unsafeCheckTableContainsId("genre", g.getId())) {
                    throw new NotFoundException("Жанр с id = " + g.getId() + " не найден");
                }
                if (!genres.contains(g)) {
                    genres.add(g);
                }
            }
        }

        Long id = insert(
                FILM_ADD,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpa);

        genreDbStorage.updateFilmGenres(id, genres);
        Film savedFilm = getFilmImpl(id);

        return savedFilm;
    }

    @Override
    public Film getFilm(Long id) {
        return getFilmImpl(id);
    }

    public Film updateFilm(Film newFilm) {
        ArrayList<Genre> genres = new ArrayList<>();
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (newFilm.getDescription() == null || newFilm.getDescription().isBlank()) {
            throw new ConstraintViolationException("Описание не может быть пустым");
        }
        if (!unsafeCheckTableContainsId("films", newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        if (newFilm.getMpa() != null) {
            if (!unsafeCheckTableContainsId("mpa", newFilm.getMpa().getId())) {
                throw new NotFoundException("Mpa с id = " + newFilm.getMpa().getId() + " не найден");
            }
        }
        if (newFilm.getGenres() != null) {
            for (Genre g : newFilm.getGenres()) {
                if (!unsafeCheckTableContainsId("genre", g.getId())) {
                    throw new NotFoundException("Жанр с id = " + g.getId() + " не найден");
                }
                if (!genres.contains(g)) {
                    genres.add(g);
                }
            }
        }

        update(FILM_UPDATE,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId());

        genreDbStorage.updateFilmGenres(newFilm.getId(), genres);
        Film savedFilm = getFilmImpl(newFilm.getId());
        return savedFilm;
    }

    @Override
    public void deleteFilm(Long id) {
        delete(FILM_DELETE, id);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FILM_GET_ALL);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return findMany(LIKES_FILMS_DESC);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        insert(LIKE_ADD, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (!unsafeCheckTableContainsId("users", userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!unsafeCheckTableContainsId("films", filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        update(LIKE_DELETE, filmId, userId);
    }

    private Film getFilmImpl(Long id) {
        if (!unsafeCheckTableContainsId("films", id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return findOne(FILM_GET_BY_ID, id).get();
    }
}
