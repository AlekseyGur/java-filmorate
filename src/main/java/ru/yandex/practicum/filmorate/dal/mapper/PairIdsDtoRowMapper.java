package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.PairIdsDto;

@Slf4j
@Component
public class PairIdsDtoRowMapper implements RowMapper<PairIdsDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public PairIdsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PairIdsDto pair = new PairIdsDto();
        pair.setFirst(rs.getLong(1));
        pair.setLast(rs.getLong(2));
        return pair;
    }
}
