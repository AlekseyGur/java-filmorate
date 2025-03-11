package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "name" })
public class GenreDto {
    private Long id;
    private String name;
}