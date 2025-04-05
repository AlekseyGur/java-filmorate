package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class FilmDbStorage extends BaseRepository<FilmDto> implements FilmStorage {

    private static final String FILM_ADD = "INSERT INTO films(name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?);";
    private static final String FILM_GET_BY_ID = "SELECT * FROM films WHERE id = ?;";
    private static final String FILMS_GET_BY_IDS = "SELECT * FROM films WHERE id IN (?);";
    private static final String FILM_GET_ALL = "SELECT * FROM films;";
    private static final String FILM_UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id = ?;";
    private static final String FILM_DELETE = "DELETE FROM films WHERE id = ? LIMIT 1;";
    private static final String LIKES_FILMS_DESC = "SELECT f.*, COUNT(l.*) AS likes_count FROM films f LEFT JOIN films_likes l ON f.id = l.film_id GROUP BY f.id ORDER BY likes_count DESC;";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<FilmDto> addFilm(Film film) {
        Long mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();
        Long id = insert(
                FILM_ADD,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId);

        return Optional.ofNullable(getFilmImpl(id).orElse(null));
    }

    @Override
    public Optional<FilmDto> getFilm(Long id) {
        return Optional.ofNullable(getFilmImpl(id).orElse(null));
    }

    @Override
    public void deleteFilm(Long id) {
        delete(FILM_DELETE, id);
    }

    @Override
    public List<FilmDto> findAll() {
        return findMany(FILM_GET_ALL);
    }

    @Override
    public List<FilmDto> getPopularFilms(Integer count) {
        return findMany(LIKES_FILMS_DESC);
    }

    @Override
    public List<FilmDto> getFilms(List<Long> ids) {
        String q = ids.stream().map(id -> id.toString()).collect(Collectors.joining(","));
        return findMany(FILMS_GET_BY_IDS, q);
    }

    @Override
    public Optional<FilmDto> updateFilm(Film film) {
        Long mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();
        update(FILM_UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId());

        return Optional.ofNullable(getFilmImpl(film.getId()).orElse(null));
    }

    private Optional<FilmDto> getFilmImpl(Long id) {
        return findOne(FILM_GET_BY_ID, id);
    }
}
