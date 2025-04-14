package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    boolean checkLikeExist(Long filmId, Long userId);

    HashMap<Long, List<Long>> findAllByFilmIds(List<Long> filmsIds);
}