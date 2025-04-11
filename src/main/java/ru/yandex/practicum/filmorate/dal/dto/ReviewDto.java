package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id;
    private Long filmId;
    private Long userId;
    private Boolean isPositive;
    private String content;
    private Integer useful = 0;
}
