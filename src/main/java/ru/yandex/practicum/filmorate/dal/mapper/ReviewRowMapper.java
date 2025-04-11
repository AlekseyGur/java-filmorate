package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.ReviewDto;

@Component
public class ReviewRowMapper implements RowMapper<ReviewDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public ReviewDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewDto review = new ReviewDto();
        review.setId(rs.getLong("id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUserId(rs.getLong("user_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}
