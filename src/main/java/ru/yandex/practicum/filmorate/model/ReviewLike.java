package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "reviewId", "userId", "isUseful" })
public class ReviewLike {
    private Long reviewId;
    private Long userId;
    private Boolean isUseful;
}
