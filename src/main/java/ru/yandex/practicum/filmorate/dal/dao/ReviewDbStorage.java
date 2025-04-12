package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dal.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class ReviewDbStorage extends BaseRepository<ReviewDto> implements ReviewStorage {

    private static final String INSERT_REVIEW = "INSERT INTO reviews (film_id, user_id, content, is_positive) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?;";
    private static final String DELETE_BY_ID = "DELETE FROM reviews WHERE id = ?;";

    private static final String BEGIN = """
            SELECT
                r.id AS id,
                r.film_id AS film_id,
                r.user_id AS user_id,
                r.content AS content,
                r.is_positive AS is_positive,
                2 * SUM(rl.is_useful) - COUNT(rl.*) AS useful
            FROM reviews AS r
            LEFT JOIN reviews_likes AS rl
            ON r.id = rl.review_id """;
    private static final String END = """
            GROUP BY
                r.id,
                r.film_id,
                r.user_id,
                r.content,
                r.is_positive
            ORDER BY useful DESC, id ASC
            LIMIT ?; """;
    private static final String FIND_BY_ID_WITH_RATING = BEGIN + " WHERE r.id = ? " + END;
    private static final String FIND_BY_FILM_ID_WITH_RATING = BEGIN + " WHERE r.film_id = ? " + END;
    private static final String FIND_ALL = BEGIN + " " + END;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<ReviewDto> add(Review review) {
        Long id = insert(
                INSERT_REVIEW,
                review.getFilmId(),
                review.getUserId(),
                review.getContent(),
                review.getIsPositive());

        return Optional.ofNullable(getByIdImpl(id).orElse(null));
    }

    @Override
    public void remove(Long id) {
        delete(DELETE_BY_ID, id);
    }

    @Override
    public Optional<ReviewDto> get(Long id) {
        return Optional.ofNullable(getByIdImpl(id).orElse(null));
    }

    @Override
    public List<ReviewDto> getByFilmId(Long filmId, Integer count) {
        return findMany(FIND_BY_FILM_ID_WITH_RATING, filmId, count);
    }

    @Override
    public List<ReviewDto> findAll(Integer count) {
        return findMany(FIND_ALL, count);
    }

    @Override
    public Optional<ReviewDto> update(Review review) {
        update(UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return Optional.ofNullable(getByIdImpl(review.getReviewId()).orElse(null));
    }

    private Optional<ReviewDto> getByIdImpl(Long id) {
        int count = 1;
        return findOne(FIND_BY_ID_WITH_RATING, id, count);
    }
}
