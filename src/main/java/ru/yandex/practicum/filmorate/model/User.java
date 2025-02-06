package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "login" })
public class User {
    private Long id;

    @Email
    private String email;
    
    @NotBlank
    private String login;
    private String name;
    private String birthday;
}