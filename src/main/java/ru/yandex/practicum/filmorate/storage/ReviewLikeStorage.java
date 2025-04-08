package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewLike;

public interface ReviewLikeStorage {

    void add(ReviewLike like);

    void remove(Long reviewId, Long userId, Boolean likeOrDislike);
}