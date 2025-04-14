package ru.yandex.practicum.filmorate.dal.mapper;

import java.util.List;

import ru.yandex.practicum.filmorate.dal.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

public class FeedMapper {
    public static Feed fromDto(FeedDto feedDto) {
        Feed feed = new Feed();
        feed.setEntityId(feedDto.getEntityId());
        feed.setEventId(feedDto.getEventId());
        feed.setEventType(feedDto.getEventType());
        feed.setOperation(feedDto.getOperation());
        feed.setTimestamp(feedDto.getTimestamp());
        feed.setUserId(feedDto.getUserId());
        return feed;
    }

    public static List<Feed> fromDto(List<FeedDto> feedDto) {
        return feedDto.stream().map(FeedMapper::fromDto).toList();
    }
}
