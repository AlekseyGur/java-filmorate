package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Autowired
    private MpaStorage mpaStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private UserDbStorage userStorage;

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getString("release_date"));
        film.setDuration(rs.getInt("duration"));

        // Получаем Mpa
        film.setMpa(mpaStorage.getMpa(rs.getLong("mpa")));

        // Получаем жанры
        film.setGenres(genreStorage.findAllByFilmId(film.getId()));

        // Получаем лайки
        film.setLikes(userStorage.getFilmLikesUsersIds(film.getId()));

        return film;
    }
}
