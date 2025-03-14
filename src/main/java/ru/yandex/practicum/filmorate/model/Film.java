package ru.yandex.practicum.filmorate.model;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "name", "releaseDate" })
public class Film {
    public static final int MAX_DESCRIPTION_LEN = 200;
    public static final String MIN_RELEASE_DATE = "1895-12-28";

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private String releaseDate;

    @Min(1)
    private Integer duration;

    private Mpa mpa;
    private List<Genre> genres;
    private List<Long> likes;
}