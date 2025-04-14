package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.utils.Validate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review update(@RequestBody Review review) {
        Validate.review(review);
        return reviewService.update(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updatePut(@RequestBody Review review) {
        // тесты postman требуют PUT по разным адресам
        Validate.review(review);
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        reviewService.remove(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@RequestBody Review review) {
        Validate.review(review);
        return reviewService.add(review);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getPost(@PathVariable Long id) {
        // тесты postman требуют POST по разным адресам
        return reviewService.get(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review get(@PathVariable Long id) {
        return reviewService.get(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getFilmReviews(@RequestParam(required = false) Long filmId,
                    @RequestParam(defaultValue = "10") int count) {
        return reviewService.getFilmReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewLikeService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewLikeService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewLikeService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewLikeService.removeDislike(reviewId, userId);
    }

}
