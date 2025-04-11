package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.dal.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {
    Optional<DirectorDto> add(Director director);

    Optional<DirectorDto> get(Long id);

    Optional<DirectorDto> update(Director director);

    void updateFilmDirectors(Long filmId, List<Long> directorsIds);

    void delete(Long id);

    List<DirectorDto> findAll();

    List<DirectorDto> get(List<Long> directorsIds);

    HashMap<Long, List<Long>> findAllDirectorsByFilmIds(List<Long> filmsIds);
}