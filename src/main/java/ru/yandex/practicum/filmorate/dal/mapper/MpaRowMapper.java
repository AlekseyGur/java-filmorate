package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Mpa mpa = new Mpa(rs.getLong("id"), rs.getString("name"));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}