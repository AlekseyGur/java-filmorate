package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "login" })
public class User {
    private Long id;
    private String email;

    private String login;
    private String name;
    private String birthday;
}