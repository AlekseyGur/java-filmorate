package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = { "name", "releaseDate" })
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private Integer duration;
    private Mpa mpa;
    private Long rate;
    private Set<Long> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();
}