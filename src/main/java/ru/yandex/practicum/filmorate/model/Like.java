package ru.yandex.practicum.filmorate.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "userId", "filmId" })
public class Like implements Serializable {
    private Long userId;
    private Long filmId;
}