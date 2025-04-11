package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "filmId", "userId", "content", "isPositive" })
public class Review {
    private Long reviewId;
    private Long filmId;
    private Long userId;
    private Boolean isPositive;
    private String content;
    private Integer useful = 0;
}
