package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    // Добавляет новый фильм в хранилище
    Film addFilm(Film film);

    // Получает фильм по его уникальному идентификатору
    Film getFilm(Long id);

    // Обновляет информацию о фильме
    Film updateFilm(Film film);

    // Удаляет фильм по его уникальному идентификатору
    void deleteFilm(Long id);

    // Получает список всех фильмов
    List<Film> findAll();

    // Пользователь ставит лайк фильму
    void addLike(Long filmId, Long userId);

    // Пользователь удаляет лайк у фильма
    void removeLike(Long filmId, Long userId);

    // Получает список популярных фильмов
    List<Film> getPopularFilms(Integer count);
}