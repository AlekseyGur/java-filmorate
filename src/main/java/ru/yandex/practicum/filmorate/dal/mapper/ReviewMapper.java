package ru.yandex.practicum.filmorate.dal.mapper;

import java.util.List;

import ru.yandex.practicum.filmorate.dal.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {
    public static Review fromDto(ReviewDto reviewDto) {
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

    public static List<Review> fromDto(List<ReviewDto> reviewDto) {
        return reviewDto.stream().map(ReviewMapper::fromDto).toList();
    }
}
