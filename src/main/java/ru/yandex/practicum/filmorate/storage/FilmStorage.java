package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    // Добавляет новый фильм в хранилище
    FilmDto addFilm(Film film);

    // Получает фильм по его уникальному идентификатору
    FilmDto getFilm(Long id);

    // Получает список фильмов по id
    List<FilmDto> getFilms(List<Long> filmsIds);

    // Обновляет информацию о фильме
    FilmDto updateFilm(Film film);

    // Удаляет фильм по его уникальному идентификатору
    void deleteFilm(Long id);

    // Получает список всех фильмов
    List<FilmDto> findAll();

    // Получает список популярных фильмов
    List<FilmDto> getPopularFilms(Integer count);
}