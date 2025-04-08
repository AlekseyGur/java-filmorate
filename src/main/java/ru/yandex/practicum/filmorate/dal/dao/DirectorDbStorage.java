package ru.yandex.practicum.filmorate.dal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dal.dto.PairIdsDto;
import ru.yandex.practicum.filmorate.dal.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

@Slf4j
@Component
@Repository
public class DirectorDbStorage extends BaseRepository<DirectorDto> implements DirectorStorage {
    private static final String ADD = "INSERT INTO directors(name) VALUES (?);";
    private static final String GET_BY_ID = "SELECT * FROM directors WHERE id = ?;";
    private static final String GET_BY_IDS = "SELECT * FROM directors WHERE id IN (:ids);";
    private static final String GET_ALL = "SELECT * FROM directors;";
    private static final String UPDATE = "UPDATE directors SET name = ? WHERE id = ?;";
    private static final String DELETE = "DELETE FROM directors WHERE id = ? LIMIT 1;";

    private static final String GET_DIRECTORS_BY_FILMS_IDS = "SELECT film_id, director_id FROM films_directors WHERE film_id IN (:ids);";
    private static final String DIRECTORS_ADD_MANY = "INSERT INTO films_directors(film_id, director_id) VALUES (:filmId, :directorId)";
    private static final String DIRECTORS_FILM_DELETE_ALL = "DELETE FROM films_directors WHERE film_id = ?;";

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<DirectorDto> add(Director director) {
        long id = insert(ADD, director.getName());

        return Optional.ofNullable(getUserImpl(id).orElse(null));
    }

    @Override
    public void updateFilmDirectors(Long filmId, List<Long> directors) {
        deleteFilmDirectorsAllImpl(filmId);

        if (directors.isEmpty()) {
            return;
        }

        List<SqlParameterSource> paramsList = directors.stream()
                .map(directorId -> new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("directorId", directorId))
                .collect(Collectors.toList());

        insertMany(DIRECTORS_ADD_MANY, paramsList.toArray(new SqlParameterSource[0]));
    }

    @Override
    public Optional<DirectorDto> get(Long id) {
        return Optional.ofNullable(getUserImpl(id).orElse(null));
    }

    @Override
    public Optional<DirectorDto> update(Director director) {
        update(UPDATE,
                director.getName(),
                director.getId());

        return Optional.ofNullable(getUserImpl(director.getId()).orElse(null));
    }

    @Override
    public void delete(Long id) {
        delete(DELETE, id);
    }

    @Override
    public List<DirectorDto> findAll() {
        return findMany(GET_ALL);
    }

    @Override
    public List<DirectorDto> get(List<Long> directorsIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", directorsIds);
        return findManySqlParameterSource(GET_BY_IDS, parameters);
    }

    @Override
    public HashMap<Long, List<Long>> findAllDirectorsByFilmIds(List<Long> filmsIds) {
        HashMap<Long, List<Long>> res = new HashMap<>();
        SqlParameterSource parameters = new MapSqlParameterSource("ids", filmsIds);
        for (PairIdsDto row : findManyIdToId(GET_DIRECTORS_BY_FILMS_IDS, parameters)) {
            Long filmId = row.getFirst();
            Long directorId = row.getLast();
            res.computeIfAbsent(filmId, k -> new ArrayList<>()).add(directorId);
        }
        return res;
    }

    private Optional<DirectorDto> getUserImpl(Long id) {
        return findOne(GET_BY_ID, id);
    }

    private void deleteFilmDirectorsAllImpl(Long filmId) {
        update(DIRECTORS_FILM_DELETE_ALL, filmId);
    }
}