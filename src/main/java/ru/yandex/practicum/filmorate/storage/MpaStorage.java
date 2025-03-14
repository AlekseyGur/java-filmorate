package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {
    Mpa addMpa(Mpa mpaId);

    void removeMpa(Long id);

    Mpa getMpa(Long id);

    List<Mpa> findAll();
}