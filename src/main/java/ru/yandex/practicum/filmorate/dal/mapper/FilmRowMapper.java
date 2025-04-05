package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.FilmDto;

@Component
public class FilmRowMapper implements RowMapper<FilmDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public FilmDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmDto film = new FilmDto();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getString("release_date"));
        film.setDuration(rs.getInt("duration"));
        film.setMpa(rs.getLong("mpa"));
        return film;
    }
}
