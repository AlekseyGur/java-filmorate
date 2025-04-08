package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;

@Data
public class ReviewLikeDto {
    private Long reviewId;
    private Long userId;
    private Boolean isUseful;
}
