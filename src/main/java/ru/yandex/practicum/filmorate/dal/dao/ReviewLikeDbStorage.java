package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.ReviewLikeDto;
import ru.yandex.practicum.filmorate.dal.mapper.ReviewLikeRowMapper;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class ReviewLikeDbStorage extends BaseRepository<ReviewLikeDto> implements ReviewLikeStorage {
    private static final String ADD_LIKE = "INSERT INTO reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, ?);";
    private static final String DELETE_LIKE = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_useful = ? LIMIT 1;";

    @Autowired
    public ReviewLikeDbStorage(JdbcTemplate jdbc, ReviewLikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void add(ReviewLike like) {
        insert(ADD_LIKE,
                like.getReviewId(),
                like.getUserId(),
                like.getIsUseful());
    }

    @Override
    public void remove(Long reviewId, Long userId, Boolean likeOrDislike) {
        update(DELETE_LIKE, reviewId, userId, likeOrDislike);
    }
}
