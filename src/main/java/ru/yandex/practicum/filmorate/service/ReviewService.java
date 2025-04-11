package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

@Slf4j
@Component
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
        Review reviewSaved = convertReviewDtoToReview(reviewStorage.add(review).orElse(null));
        feedService.add(review.getUserId(), reviewSaved.getReviewId(), FeedEventType.REVIEW, FeedOperation.ADD);
        return reviewSaved;
    }

    public Review update(Review review) {
        checkReviewNotNullAndIdExistOrThrowIfNot(review.getReviewId());
        filmService.checkFilmNotNullAndIdExistOrThrowIfNot(review.getFilmId());
        userService.checkUserNotNullAndIdExistOrThrowIfNot(review.getUserId());
        feedService.add(review.getUserId(), review.getReviewId(), FeedEventType.REVIEW, FeedOperation.UPDATE);
        return convertReviewDtoToReview(reviewStorage.update(review).orElse(null));
    }

    public void remove(Long id) {
        checkReviewNotNullAndIdExistOrThrowIfNot(id);
        Review review = convertReviewDtoToReview(reviewStorage.get(id).orElse(null));
        feedService.add(review.getUserId(), review.getReviewId(), FeedEventType.REVIEW, FeedOperation.REMOVE);
        reviewStorage.remove(id);
    }

    public Review get(Long id) {
        Review review = convertReviewDtoToReview(reviewStorage.get(id).orElse(null));
        checkReviewNotNullThrowIfNot(review);
        return review;
    }

    public List<Review> getFilmReviews(Long filmId, int count) {
        if (filmId != null) {
            return convertReviewDtoToReview(reviewStorage.getByFilmId(filmId, count));
        }
        return convertReviewDtoToReview(reviewStorage.findAll(count));
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

    private List<Review> convertReviewDtoToReview(List<ReviewDto> reviewsDto) {
        return reviewsDto.stream().map(this::convertReviewDtoToReview).collect(Collectors.toList());
    }

    private Review convertReviewDtoToReview(ReviewDto reviewDto) {
        if (reviewDto == null) {
            return null;
        }
        Review review = new Review();
        review.setReviewId(reviewDto.getId());
        review.setFilmId(reviewDto.getFilmId());
        review.setUserId(reviewDto.getUserId());
        review.setIsPositive(reviewDto.getIsPositive());
        review.setContent(reviewDto.getContent());
        review.setUseful(reviewDto.getUseful());
        return review;
    }
}
