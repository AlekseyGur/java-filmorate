package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeService likeService;
    private final DirectorService directorService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final ToolsDb toolsDb;

    public List<Film> search(String query, String by) {
        query = query.toLowerCase();
        List<FilmDto> films = switch (by) {
            case "title" -> filmStorage.searchByTitle(query);
            case "director" -> filmStorage.searchByDirector(query);
            default -> filmStorage.searchByTitleOrDirector(query);
        };

        return addMetaInfoToFilms(films);
    }

    public List<Film> getPopularFilms(int count) {
        List<FilmDto> films = filmStorage.getPopularFilms(count);
        return addMetaInfoToFilms(films);
    }

    public Film getFilm(Long filmId) {
        checkFilmNotNullAndIdExistOrThrowIfNot(filmId);
        return getFilmsImpl(List.of(filmId)).get(0);
    }

    public List<Film> getRecommendedFilms(Long userId) {
        return addMetaInfoToFilms(filmStorage.getRecommendedFilms(userId));
    }

    public List<Film> findAll() {
        List<FilmDto> films = filmStorage.findAll();
        return addMetaInfoToFilms(films);
    }

    public Film create(Film film) {
        checkMpaExistThrowIfNot(film);
        List<Long> genresIds = filterGenreThrowIfNotExist(film);
        List<Long> directorsIds = filterDirectorThrowIfNotExist(film);

        FilmDto savedFilmDto = filmStorage.addFilm(film).orElse(null);
        updateFilmGenresIds(savedFilmDto.getId(), genresIds);
        updateFilmDirectorsIds(savedFilmDto.getId(), directorsIds);

        return getFilmImpl(savedFilmDto.getId());
    }

    public Film update(Film film) {
        checkFilmIdAndDescrNotNullOrThrowIfNot(film);
        checkFilmNotNullAndIdExistOrThrowIfNot(film);

        checkMpaExistThrowIfNot(film);
        List<Long> genresIds = filterGenreThrowIfNotExist(film);
        List<Long> directorsIds = filterDirectorThrowIfNotExist(film);

        FilmDto savedFilmDto = filmStorage.updateFilm(film).orElse(null);
        updateFilmGenresIds(savedFilmDto.getId(), genresIds);
        updateFilmDirectorsIds(savedFilmDto.getId(), directorsIds);

        return getFilmImpl(savedFilmDto.getId());
    }

    public List<Film> findByDirectorSort(Long directorId, String sortBy) {
        directorService.checkDirectorNotNullAndIdExistOrThrowIfNot(directorId);
        if (!(sortBy.equals("year") || sortBy.equals("likes"))) {
            throw new ConditionsNotMetException("Необходимо передать данные фильма");
        }
        return addMetaInfoToFilms(filmStorage.findAllByDirectorIdSort(directorId, sortBy));
    }

    public List<Film> getCommonFilmsWithFriend(Long userId, Long friendId) {
        return addMetaInfoToFilms(filmStorage.getCommonFilmsWithFriend(userId, friendId));
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

    private void updateFilmDirectorsIds(Long filmId, List<Long> directorsIds) {
        if (filmId != null) {
            directorService.updateFilmDirectors(filmId, directorsIds);
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

    private List<Long> filterDirectorThrowIfNotExist(Film film) {
        if (film.getDirectors() != null) {
            List<Long> directorsObj = film.getDirectors().stream().map(x -> x.getId()).toList();
            return directorService.filterDirectorsThrowIfNotExist(directorsObj);
        }
        return List.of();
    }

    private Film addMetaInfoToFilm(FilmDto film) {
        return addMetaInfoToFilms(List.of(film)).get(0);
    }

    private HashMap<Long, List<Director>> getDirectorsForFilms(List<Long> filmsIds) {
        HashMap<Long, List<Director>> res = new HashMap<>();
        if (filmsIds.isEmpty()) {
            return res;
        }

        HashMap<Long, List<Long>> directors = directorService.findAllDirectorsByFilmIds(filmsIds);
        List<Long> directorsIdsAll = directors.values().stream().flatMap(List::stream).distinct().toList();
        HashMap<Long, Director> directorsAllInfo = directorService.get(directorsIdsAll).stream()
                .collect(Collectors.toMap(
                        Director::getId,
                        director -> director,
                        (oldValue, newValue) -> oldValue,
                        HashMap::new));

        for (Map.Entry<Long, List<Long>> entry : directors.entrySet()) {
            Long filmId = entry.getKey();
            List<Director> filmDirectors = entry.getValue()
                    .stream()
                    .map(x -> directorsAllInfo.get(x))
                    .toList();
            res.computeIfAbsent(filmId, k -> new ArrayList<>()).addAll(filmDirectors);
        }

        return res;
    }

    private List<Film> addMetaInfoToFilms(List<FilmDto> films) {
        List<Long> filmsIds = films.stream().map(x -> x.getId()).toList();
        HashMap<Long, List<Genre>> filmsGenres = genreService.getGenresForFilm(filmsIds);
        HashMap<Long, List<Long>> filmsLikes = likeService.getLikesForFilms(filmsIds);
        HashMap<Long, List<Director>> filmsDirectors = getDirectorsForFilms(filmsIds);
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
            newFilm.setDirectors(filmsDirectors.getOrDefault(id, List.of()));
            newFilm.setMpa(mpas.getOrDefault(filmDto.getMpa(), null));

            res.add(newFilm);
        }

        return res;
    }

    private Film getFilmImpl(Long filmId) {
        FilmDto film = filmStorage.getFilm(filmId).orElse(null);
        return addMetaInfoToFilm(film);
    }

    private List<Film> getFilmsImpl(List<Long> filmsIds) {
        List<FilmDto> films = filmStorage.getFilms(filmsIds);
        return addMetaInfoToFilms(films);
    }
}
