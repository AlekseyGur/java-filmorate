package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Optional<FilmDto> addFilm(Film film);

    Optional<FilmDto> getFilm(Long id);

    List<FilmDto> getFilms(List<Long> filmsIds);

    List<FilmDto> getRecommendedFilms(Long userId);

    Optional<FilmDto> updateFilm(Film film);

    void deleteFilm(Long id);

    List<FilmDto> findAll();

    List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<FilmDto> searchByTitle(String query);

    List<FilmDto> searchByDirector(String query);

    List<FilmDto> searchByTitleOrDirector(String query);

    List<FilmDto> findAllByDirectorIdSort(Long directorId, String sortBy);

    List<FilmDto> getCommonFilmsWithFriend(Long userId, Long friendId);
}