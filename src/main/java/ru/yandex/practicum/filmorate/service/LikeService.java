package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Component
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;
    private final FeedService feedService;
    private final ToolsDb toolsDb;

    public HashMap<Long, List<Long>> getLikesForFilms(List<Long> filmsIds) {
        return likeStorage.findAllByFilmIds(filmsIds);
    }

    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
        feedService.add(userId, filmId, FeedEventType.LIKE, FeedOperation.ADD);
    }

    public void removeLike(Long filmId, Long userId) {
        checkUserNotNullAndIdExistOrThrowIfNot(userId);
        checkFilmNotNullAndIdExistOrThrowIfNot(filmId);
        likeStorage.removeLike(filmId, userId);
        feedService.add(userId, filmId, FeedEventType.LIKE, FeedOperation.REMOVE);
    }

    private void checkUserNotNullAndIdExistOrThrowIfNot(Long id) {
        if (id == null || !toolsDb.unsafeCheckTableContainsId("users", id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private void checkFilmNotNullAndIdExistOrThrowIfNot(Long filmId) {
        if (filmId == null || !toolsDb.unsafeCheckTableContainsId("films", filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }
}
