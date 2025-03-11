package ru.yandex.practicum.filmorate.dal.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import org.springframework.stereotype.Component;

import org.springframework.jdbc.core.RowMapper;

@Slf4j
@Component
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String GENRE_ADD = "INSERT INTO genre(name) VALUES (?);";
    private static final String GENRE_GET_BY_ID = "SELECT * FROM genre WHERE id = ?";
    private static final String GENRE_GET_ALL = "SELECT * FROM genre ORDER BY id ASC;";
    private static final String GENRE_DELETE = "DELETE FROM genre WHERE id = ? LIMIT 1;";
    private static final String GENRE_GET_BY_FILM_ID = "SELECT g.* FROM films_genres fg JOIN genre g ON fg.genre_id = g.id WHERE fg.film_id = ?;";
    private static final String GENRE_FILM_ADD = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?);";
    private static final String GENRE_FILM_DELETE_ALL = "DELETE FROM films_genres WHERE film_id = ?;";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Genre addGenre(Genre genre) {
        long id = insert(GENRE_ADD, genre.getName());
        return findOne(GENRE_GET_BY_ID, id).get();
    }

    @Override
    public void removeGenre(Long genreId) {
        update(GENRE_DELETE, genreId);
    }

    @Override
    public Genre getGenre(Long id) {
        if (!unsafeCheckTableContainsId("genre", id)) {
            throw new NotFoundException(":Жанр с id = " + id + " не найден");
        }
        return findOne(GENRE_GET_BY_ID, id).get();
    }

    @Override
    public List<Genre> findAll() {
        return findMany(GENRE_GET_ALL);
    }

    public List<Genre> findAllByFilmId(Long filmId) {
        return findMany(GENRE_GET_BY_FILM_ID, filmId);
    }

    public Genre setFilmGenre(Long filmId, Long genreId) {
        long id = insert(GENRE_FILM_ADD, filmId, genreId);
        return findOne(GENRE_GET_BY_ID, id).get();
    }

    public void deleteFilmGenreAll(Long filmId) {
        update(GENRE_FILM_DELETE_ALL, filmId);
    }

    public List<Genre> updateFilmGenres(Long filmId, List<Genre> genres) {
        setFilmGenre(filmId, genres);
        return findAllByFilmId(filmId);
    }

    public void setFilmGenre(Long filmId, List<Genre> genres) {
        deleteFilmGenreAll(filmId);
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                insert(GENRE_FILM_ADD, filmId, genre.getId());
            }
        }
    }
}