package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private Integer duration;
    private Long mpa;
}