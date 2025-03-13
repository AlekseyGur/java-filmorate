package ru.yandex.practicum.filmorate.dal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.PairIdsDto;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String GENRE_ADD = "INSERT INTO genre(name) VALUES (?);";
    private static final String GENRE_GET_BY_ID = "SELECT * FROM genre WHERE id = ?";
    private static final String GENRE_GET_ALL = "SELECT * FROM genre ORDER BY id ASC;";
    private static final String GENRE_DELETE = "DELETE FROM genre WHERE id = ? LIMIT 1;";
    private static final String GENRE_GET_BY_FILM_ID = "SELECT g.* FROM films_genres fg JOIN genre g ON fg.genre_id = g.id WHERE fg.film_id = ?;";
    private static final String GENRE_GET_BY_FILMS_IDS = "SELECT film_id, genre_id FROM films_genres WHERE film_id IN (:ids);";
    private static final String GENRE_FILM_ADD_MANY = "INSERT INTO films_genres(film_id, genre_id) VALUES (:filmId, :genreId)";
    private static final String GENRE_FILM_DELETE_ALL = "DELETE FROM films_genres WHERE film_id = ?;";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Genre addGenre(Genre genre) {
        long id = insert(GENRE_ADD, genre.getName());
        return findOne(GENRE_GET_BY_ID, id).orElse(null);
    }

    @Override
    public void removeGenre(Long genreId) {
        update(GENRE_DELETE, genreId);
    }

    @Override
    public Genre getGenre(Long id) {
        return findOne(GENRE_GET_BY_ID, id).orElse(null);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(GENRE_GET_ALL);
    }

    @Override
    public List<Genre> findAllByFilmId(Long filmId) {
        return findMany(GENRE_GET_BY_FILM_ID, filmId);
    }

    @Override
    public void deleteFilmGenreAll(Long filmId) {
        deleteFilmGenreAllImpl(filmId);
    }

    @Override
    public List<Genre> updateFilmGenres(Long filmId, List<Long> genres) {
        setFilmGenresImpl(filmId, genres);
        return findAllByFilmId(filmId);
    }

    @Override
    public HashMap<Long, List<Genre>> findAllByFilmIds(List<Long> filmsIds) {
        HashMap<Long, Genre> genresInfo = new HashMap<>();

        for (Genre genre : findAll()) {
            genresInfo.put(genre.getId(), genre);
        }

        SqlParameterSource parameters = new MapSqlParameterSource("ids", filmsIds);

        HashMap<Long, List<Genre>> res = new HashMap<>();
        for (PairIdsDto row : findManyIdToId(GENRE_GET_BY_FILMS_IDS, parameters)) {
            Long filmId = row.getFirst();
            Long genreId = row.getLast();
            Genre genre = genresInfo.get(genreId);
            res.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        }
        return res;
    }

    private void setFilmGenresImpl(Long filmId, List<Long> genres) {
        if (genres.isEmpty()) {
            return;
        }

        deleteFilmGenreAllImpl(filmId);

        // Создаем список мап для каждого значения
        List<SqlParameterSource> paramsList = genres.stream()
                .map(genreId -> new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("genreId", genreId))
                .collect(Collectors.toList());

        insertMany(GENRE_FILM_ADD_MANY, paramsList.toArray(new SqlParameterSource[0]));
    }

    private void deleteFilmGenreAllImpl(Long filmId) {
        update(GENRE_FILM_DELETE_ALL, filmId);
    }
}