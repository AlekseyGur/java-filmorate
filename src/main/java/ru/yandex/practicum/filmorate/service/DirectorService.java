package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dao.ToolsDb;
import ru.yandex.practicum.filmorate.dal.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final ToolsDb toolsDb;

    public List<Director> findAll() {
        return convertDirectorDtoToDirector(directorStorage.findAll());
    }

    public Director get(Long id) {
        checkDirectorNotNullAndIdExistOrThrowIfNot(id);
        return convertDirectorDtoToDirector(directorStorage.get(id).orElse(null));
    }

    public List<Director> get(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return convertDirectorDtoToDirector(directorStorage.get(ids));
    }

    public Director add(Director director) {
        return convertDirectorDtoToDirector(directorStorage.add(director).orElse(null));
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public Director update(Director director) {
        checkDirectorNotNullAndIdExistOrThrowIfNot(director);
        return convertDirectorDtoToDirector(directorStorage.update(director).orElse(null));
    }

    public void updateFilmDirectors(Long filmId, List<Long> directorsIds) {
        filterDirectorsThrowIfNotExist(directorsIds);
        directorStorage.updateFilmDirectors(filmId, directorsIds);
    }

    public List<Director> convertDirectorDtoToDirector(List<DirectorDto> directorsDto) {
        return directorsDto.stream().map(this::convertDirectorDtoToDirector).collect(Collectors.toList());
    }

    public Director convertDirectorDtoToDirector(DirectorDto directorDto) {
        Director director = new Director();
        director.setId(directorDto.getId());
        director.setName(directorDto.getName());
        return director;
    }

    public void checkDirectorNotNullAndIdExistOrThrowIfNot(Long id) {
        if (!toolsDb.unsafeCheckTableContainsId("directors", id)) {
            throw new NotFoundException("Режиссёр с id = " + id + " не найден");
        }
    }

    public void checkDirectorNotNullAndIdExistOrThrowIfNot(Director director) {
        if (director == null) {
            throw new ConditionsNotMetException("Необходимо передать данные режиссёра");
        }
        checkDirectorNotNullAndIdExistOrThrowIfNot(director.getId());
    }

    public List<Long> filterDirectorsThrowIfNotExist(List<Long> directorsIds) {
        List<Long> directorsIdsFiltered = null;

        if (directorsIds != null) {
            directorsIdsFiltered = directorsIds.stream().filter(x -> x != null).distinct().toList();

            if (!directorsIdsFiltered.isEmpty()
                    && !toolsDb.unsafeCheckTableContainsId("directors", directorsIdsFiltered)) {
                throw new NotFoundException(
                        "В списке режиссеров найден неизвестный id режиссера: " + directorsIdsFiltered.toString());
            }
        }

        return directorsIdsFiltered;
    }

    public HashMap<Long, List<Long>> findAllDirectorsByFilmIds(List<Long> filmsIds) {
        return directorStorage.findAllDirectorsByFilmIds(filmsIds);
    }
}