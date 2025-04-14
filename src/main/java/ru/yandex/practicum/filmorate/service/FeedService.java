package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final ToolsDb toolsDb;

    public Feed add(Long userId, Long entityId, FeedEventType eventType, FeedOperation operation) {
        Feed feed = new Feed(userId, entityId, eventType, operation);
        return FeedMapper.fromDto(feedStorage.add(feed).orElse(null));
    }

    public List<Feed> findByUser(Long userId) {
        checkUserNotNullAndIdExistOrThrowIfNot(userId);
        return FeedMapper.fromDto(feedStorage.findByUser(userId));
    }

    private void checkUserNotNullAndIdExistOrThrowIfNot(Long id) {
        if (!toolsDb.unsafeCheckTableContainsId("users", id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}