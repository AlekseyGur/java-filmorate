package ru.yandex.practicum.filmorate.model;

import java.util.HashSet;
import java.util.Set;

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

    // Набор уникальных лайков от пользователей
    private Set<Long> likes = new HashSet<>();

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private String releaseDate;

    @Min(1)
    private Integer duration;

    // Метод для добавления лайка
    public void addLike(Long userId) {
        likes.add(userId);
    }

    // Метод для удаления лайка
    public void removeLike(Long userId) {
        likes.remove(userId);
    }

    // Метод для получения количества лайков
    public int getLikesCount() {
        return likes.size();
    }
}