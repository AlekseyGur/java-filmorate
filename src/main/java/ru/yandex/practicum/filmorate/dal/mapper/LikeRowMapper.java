package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

@Component
public class LikeRowMapper implements RowMapper<Like> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        Like like = new Like();
        like.setFilmId(rs.getLong("film_id"));
        like.setUserId(rs.getLong("user_id"));
        return like;
    }
}