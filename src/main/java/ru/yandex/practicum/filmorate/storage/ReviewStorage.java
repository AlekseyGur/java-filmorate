package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Optional<ReviewDto> add(Review review);

    void remove(Long id);

    Optional<ReviewDto> get(Long id);

    List<ReviewDto> getByFilmId(Long filmId, Integer count);

    Optional<ReviewDto> update(Review review);
}