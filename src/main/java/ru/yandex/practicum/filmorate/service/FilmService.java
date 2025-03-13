package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeService likeService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final ToolsDb toolsDb;

    public List<Film> getPopularFilms(int count) {
        List<FilmDto> films = filmStorage.getPopularFilms(count);
        return addMetaInfoToFilms(films);
    }

    public Film getFilm(Long filmId) {
        checkFilmNotNullAndIdExistOrThrowIfNot(filmId);
        return getFilmsImpl(List.of(filmId)).get(0);
    }

    public List<Film> findAll() {
        List<FilmDto> films = filmStorage.findAll();
        return addMetaInfoToFilms(films);
    }

    public Film create(Film film) {
        checkMpaExistThrowIfNot(film);
        List<Long> genresIds = filterGenreThrowIfNotExist(film);

        FilmDto savedFilmDto = filmStorage.addFilm(film);
        updateFilmGenresIds(savedFilmDto.getId(), genresIds);

        return getFilmImpl(savedFilmDto.getId());
    }

    public Film update(Film film) {
        checkFilmIdAndDescrNotNullOrThrowIfNot(film);
        checkFilmNotNullAndIdExistOrThrowIfNot(film);

        checkMpaExistThrowIfNot(film);
        List<Long> genresIds = filterGenreThrowIfNotExist(film);

        FilmDto savedFilmDto = filmStorage.updateFilm(film);
        updateFilmGenresIds(savedFilmDto.getId(), genresIds);

        return getFilmImpl(savedFilmDto.getId());
    }

    public void checkFilmNotNullAndIdExistOrThrowIfNot(Long filmId) {
        if (!toolsDb.unsafeCheckTableContainsId("films", filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public void checkFilmNotNullAndIdExistOrThrowIfNot(Film film) {
        if (film == null) {
            throw new ConditionsNotMetException("Необходимо передать данные фильма");
        }
        checkFilmNotNullAndIdExistOrThrowIfNot(film.getId());
    }

    private void checkFilmIdAndDescrNotNullOrThrowIfNot(Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ConstraintViolationException("Описание не может быть пустым");
        }
    }

    private void updateFilmGenresIds(Long filmId, List<Long> genresIds) {
        if (filmId != null && !genresIds.isEmpty()) {
            genreService.updateFilmGenres(filmId, genresIds);
        }
    }

    private void checkMpaExistThrowIfNot(Film film) {
        Long mpa = null;
        if (film.getMpa() != null) {
            mpa = film.getMpa().getId();
            if (!toolsDb.unsafeCheckTableContainsId("mpa", mpa)) {
                throw new NotFoundException("Рейтинг с id = " + mpa + " не найден");
            }
        }
    }

    private List<Long> filterGenreThrowIfNotExist(Film film) {
        if (film.getGenres() != null) {
            List<Long> genresObj = film.getGenres().stream().map(x -> x.getId()).toList();
            return genreService.filterGenresThrowIfNotExist(genresObj);
        }
        return List.of();
    }

    private Film addMetaInfoToFilm(FilmDto film) {
        return addMetaInfoToFilms(List.of(film)).get(0);
    }

    private List<Film> addMetaInfoToFilms(List<FilmDto> films) {
        List<Long> filmsIds = films.stream().map(x -> x.getId()).toList();
        HashMap<Long, List<Genre>> filmsGenres = genreService.getGenresForFilm(filmsIds);
        HashMap<Long, List<Long>> filmsLikes = likeService.getLikesForFilms(filmsIds);
        HashMap<Long, Mpa> mpas = mpaService.findAllHashMap();

        List<Film> res = new ArrayList<>();
        for (FilmDto filmDto : films) {
            Long id = filmDto.getId();

            Film newFilm = new Film();
            newFilm.setId(id);
            newFilm.setName(filmDto.getName());
            newFilm.setDescription(filmDto.getDescription());
            newFilm.setReleaseDate(filmDto.getReleaseDate());
            newFilm.setDuration(filmDto.getDuration());

            newFilm.setGenres(filmsGenres.getOrDefault(id, null));
            newFilm.setLikes(filmsLikes.getOrDefault(id, null));
            newFilm.setMpa(mpas.getOrDefault(filmDto.getMpa(), null));

            res.add(newFilm);
        }

        return res;
    }

    private Film getFilmImpl(Long filmId) {
        FilmDto film = filmStorage.getFilm(filmId);
        return addMetaInfoToFilm(film);
    }

    private List<Film> getFilmsImpl(List<Long> filmsIds) {
        List<FilmDto> films = filmStorage.getFilms(filmsIds);
        return addMetaInfoToFilms(films);
    }
}
