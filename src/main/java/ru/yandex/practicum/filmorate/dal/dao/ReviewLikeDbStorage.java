package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dal.dto.ReviewLikeDto;
import ru.yandex.practicum.filmorate.dal.mapper.ReviewLikeRowMapper;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

@Repository
public class ReviewLikeDbStorage extends BaseRepository<ReviewLikeDto> implements ReviewLikeStorage {
    private static final String ADD_LIKE = "INSERT INTO reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, ?);";
    private static final String DELETE_LIKE = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_useful = ? LIMIT 1;";

    @Autowired
    public ReviewLikeDbStorage(NamedParameterJdbcTemplate njdbc, ReviewLikeRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public void add(ReviewLike like) {
        addImpl(like.getUserId(), like.getReviewId(), like.getIsUseful());
    }

    @Override
    public void add(Long userId, Long reviewId, Boolean isUseful) {
        addImpl(userId, reviewId, isUseful);
    }

    @Override
    public void remove(Long userId, Long reviewId, Boolean likeOrDislike) {
        update(DELETE_LIKE, reviewId, userId, likeOrDislike);
    }

    private void addImpl(Long userId, Long reviewId, Boolean isUseful) {
        insert(ADD_LIKE, reviewId, userId, isUseful);
    }
}
