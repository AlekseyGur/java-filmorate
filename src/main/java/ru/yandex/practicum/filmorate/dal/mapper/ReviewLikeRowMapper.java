package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.ReviewLikeDto;

@Component
public class ReviewLikeRowMapper implements RowMapper<ReviewLikeDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public ReviewLikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewLikeDto like = new ReviewLikeDto();
        like.setReviewId(rs.getLong("review_id"));
        like.setUserId(rs.getLong("user_id"));
        like.setIsUseful(rs.getBoolean("is_useful"));
        return like;
    }
}