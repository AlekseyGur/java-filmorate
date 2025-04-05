package ru.yandex.practicum.filmorate.dal.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;
    private Set<Long> friends;
}