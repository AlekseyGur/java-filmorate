package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;

@Data
public class FeedDto {
    private Long timestamp;
    private Long userId;
    private FeedEventType eventType;
    private FeedOperation operation;
    private Long eventId;
    private Long entityId;
}