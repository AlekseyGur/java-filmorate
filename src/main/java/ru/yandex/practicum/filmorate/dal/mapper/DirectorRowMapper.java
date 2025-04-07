package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.DirectorDto;

@Component
public class DirectorRowMapper implements RowMapper<DirectorDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public DirectorDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        DirectorDto director = new DirectorDto();
        director.setId(rs.getLong("id"));
        director.setName(rs.getString("name"));
        return director;
    }
}