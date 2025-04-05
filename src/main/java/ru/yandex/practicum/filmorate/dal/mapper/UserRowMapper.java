package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.UserDto;

@Component
public class UserRowMapper implements RowMapper<UserDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDto user = new UserDto();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getString("birthday"));
        return user;
    }
}