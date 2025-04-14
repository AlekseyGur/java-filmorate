package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedService feedService;
    private final ToolsDb toolsDb;

    public Review add(Review review) {
        filmService.checkFilmNotNullAndIdExistOrThrowIfNot(review.getFilmId());
        userService.checkUserNotNullAndIdExistOrThrowIfNot(review.getUserId());
        Review reviewSaved = ReviewMapper.fromDto(reviewStorage.add(review).orElse(null));
        feedService.add(review.getUserId(), reviewSaved.getReviewId(), FeedEventType.REVIEW, FeedOperation.ADD);
        return reviewSaved;
    }

    public Review update(Review review) {
        checkReviewNotNullAndIdExistOrThrowIfNot(review.getReviewId());
        filmService.checkFilmNotNullAndIdExistOrThrowIfNot(review.getFilmId());
        userService.checkUserNotNullAndIdExistOrThrowIfNot(review.getUserId());
        Review reviewSaved = ReviewMapper.fromDto(reviewStorage.update(review).orElse(null));
        feedService.add(reviewSaved.getUserId(), reviewSaved.getReviewId(), FeedEventType.REVIEW, FeedOperation.UPDATE);
        return reviewSaved;
    }

    public void remove(Long id) {
        checkReviewNotNullAndIdExistOrThrowIfNot(id);
        Review review = ReviewMapper.fromDto(reviewStorage.get(id).orElse(null));
        feedService.add(review.getUserId(), review.getReviewId(), FeedEventType.REVIEW, FeedOperation.REMOVE);
        reviewStorage.remove(id);
    }

    public Review get(Long id) {
        Review review = ReviewMapper.fromDto(reviewStorage.get(id).orElse(null));
        checkReviewNotNullThrowIfNot(review);
        return review;
    }

    public List<Review> getFilmReviews(Long filmId, int count) {
        if (filmId != null) {
            return ReviewMapper.fromDto(reviewStorage.getByFilmId(filmId, count));
        }
        return ReviewMapper.fromDto(reviewStorage.findAll(count));
    }

    public void checkReviewNotNullAndIdExistOrThrowIfNot(Long reviewId) {
        if (!toolsDb.unsafeCheckTableContainsId("reviews", reviewId)) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
    }

    private void checkReviewNotNullThrowIfNot(Review review) {
        if (review == null) {
            throw new NotFoundException("Отзыв на фильм не найден");
        }
    }
}
