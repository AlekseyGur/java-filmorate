package ru.yandex.practicum.filmorate.dal.mapper;

import java.util.List;

import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static User fromDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        user.setLogin(userDto.getLogin());
        return user;
    }

    public static List<User> fromDto(List<UserDto> usersDto) {
        return usersDto.stream().map(UserMapper::fromDtoToUser).toList();
    }
}
