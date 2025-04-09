package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeStorage reviewLikeStorage;
    private final ReviewService reviewService;
    private final UserService userService;
    private final FeedService feedService;
    private final ToolsDb toolsDb;

    public void addLike(Long reviewId, Long userId) {
        reviewService.checkReviewNotNullAndIdExistOrThrowIfNot(reviewId);
        userService.checkUserNotNullAndIdExistOrThrowIfNot(userId);
        reviewLikeStorage.remove(userId, reviewId, false);
        reviewLikeStorage.add(userId, reviewId, true);
        feedService.add(userId, reviewId, FeedEventType.LIKE, FeedOperation.ADD);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewService.checkReviewNotNullAndIdExistOrThrowIfNot(reviewId);
        userService.checkUserNotNullAndIdExistOrThrowIfNot(userId);
        reviewLikeStorage.remove(userId, reviewId, true);
        reviewLikeStorage.add(userId, reviewId, false);
        feedService.add(userId, reviewId, FeedEventType.LIKE, FeedOperation.ADD);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewService.checkReviewNotNullAndIdExistOrThrowIfNot(reviewId);
        userService.checkUserNotNullAndIdExistOrThrowIfNot(userId);
        reviewLikeStorage.remove(userId, reviewId, true);
        feedService.add(userId, reviewId, FeedEventType.LIKE, FeedOperation.REMOVE);
    }

    public void removeDislike(Long reviewId, Long userId) {
        reviewService.checkReviewNotNullAndIdExistOrThrowIfNot(reviewId);
        userService.checkUserNotNullAndIdExistOrThrowIfNot(userId);
        reviewLikeStorage.remove(userId, reviewId, false);
        feedService.add(userId, reviewId, FeedEventType.LIKE, FeedOperation.REMOVE);
    }

    public void checkReviewNotNullAndIdExistOrThrowIfNot(Long reviewId) {
        if (!toolsDb.unsafeCheckTableContainsId("review", reviewId)) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
    }
}