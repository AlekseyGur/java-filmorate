package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;

@Component
public class FeedRowMapper implements RowMapper<FeedDto> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public FeedDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedDto feed = new FeedDto();
        feed.setEventId(rs.getLong("id"));
        feed.setUserId(rs.getLong("user_id"));
        feed.setTimestamp(rs.getLong("timestamp"));
        feed.setEntityId(rs.getLong("entity_id"));

        feed.setEventType(FeedEventType.values()[rs.getInt("event_type")]);

        feed.setOperation(FeedOperation.values()[rs.getInt("operation")]);

        return feed;
    }
}