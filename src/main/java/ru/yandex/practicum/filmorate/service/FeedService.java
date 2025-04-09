package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.FeedDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final ToolsDb toolsDb;

    public Feed add(Long userId, Long entityId, FeedEventType eventType, FeedOperation operation) {
        Feed feed = new Feed(userId, entityId, eventType, operation);
        return convertFeedDtoToFeed(feedStorage.add(feed).orElse(null));
    }

    public List<Feed> findByUser(Long userId) {
        checkUserNotNullAndIdExistOrThrowIfNot(userId);
        return convertFeedDtoToFeed(feedStorage.findByUser(userId));
    }

    public List<Feed> convertFeedDtoToFeed(List<FeedDto> feedsDto) {
        return feedsDto.stream().map(this::convertFeedDtoToFeed).collect(Collectors.toList());
    }

    public Feed convertFeedDtoToFeed(FeedDto feedDto) {
        Feed feed = new Feed();
        feed.setEntityId(feedDto.getEntityId());
        feed.setEventId(feedDto.getEventId());
        feed.setEventType(feedDto.getEventType());
        feed.setOperation(feedDto.getOperation());
        feed.setTimestamp(feedDto.getTimestamp());
        feed.setUserId(feedDto.getUserId());
        return feed;
    }

    private void checkUserNotNullAndIdExistOrThrowIfNot(Long id) {
        if (!toolsDb.unsafeCheckTableContainsId("users", id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}