package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.List;

public interface LikeStorage {
    // Пользователь ставит лайк фильму
    void addLike(Long filmId, Long userId);

    // Пользователь удаляет лайк у фильма
    void removeLike(Long filmId, Long userId);

    HashMap<Long, List<Long>> findAllByFilmIds(List<Long> filmsIds);
}