package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "name", "releaseDate" })
public class Film {
    public static final int MAX_DESCRIPTION_LEN = 200;
    public static final String MIN_RELEASE_DATE = "1895-12-28";

    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private Integer duration;
}