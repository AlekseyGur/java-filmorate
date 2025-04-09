package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

public interface FeedStorage {
    Optional<FeedDto> add(Feed feed);

    List<FeedDto> findByUser(Long userId);
}