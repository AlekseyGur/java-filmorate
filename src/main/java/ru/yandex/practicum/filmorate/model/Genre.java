package ru.yandex.practicum.filmorate.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "id" })
public class Genre implements Serializable {
    private Long id;
    private String name;
}