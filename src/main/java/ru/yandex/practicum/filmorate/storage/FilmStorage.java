package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    // Добавляет новый фильм в хранилище
    Optional<FilmDto> addFilm(Film film);

    // Получает фильм по его уникальному идентификатору
    Optional<FilmDto> getFilm(Long id);

    // Получает список фильмов по id
    List<FilmDto> getFilms(List<Long> filmsIds);

    List<FilmDto> getRecommendedFilms(Long userId);

    // Обновляет информацию о фильме
    Optional<FilmDto> updateFilm(Film film);

    // Удаляет фильм по его уникальному идентификатору
    void deleteFilm(Long id);

    // Получает список всех фильмов
    List<FilmDto> findAll();

    // Получает список популярных фильмов
    List<FilmDto> getPopularFilms(Integer count);

    // Поиск по фильмам (по названию)
    List<FilmDto> searchByTitle(String query);

    // Поиск по фильмам (по режиссёру)
    List<FilmDto> searchByDirector(String query);

    // Поиск по фильмам (по названию и режиссёру)
    List<FilmDto> searchByTitleOrDirector(String query);

    // Получает список фильмов режиссёра с сортировкой
    List<FilmDto> findAllByDirectorIdSort(Long directorId, String sortBy);
}