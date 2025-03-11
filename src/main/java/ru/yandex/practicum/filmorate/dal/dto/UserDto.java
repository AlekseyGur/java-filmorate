package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.User;

@Data
@EqualsAndHashCode(of = { "login" })
public class UserDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;

    public User convertToUser() {
        User user = new User();
        user.setEmail(this.getEmail());
        user.setLogin(this.getLogin());
        user.setName(this.getName());
        user.setBirthday(this.getBirthday());
        return user;
    }
}