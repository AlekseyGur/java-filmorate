package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Service
public class MpaService {

    private final MpaStorage MpaStorage;

    public MpaService(MpaStorage MpaStorage) {
        this.MpaStorage = MpaStorage;
    }
    
    public Mpa get(Long id) {
        return MpaStorage.getMpa(id);
    }
    
    public Mpa create(Mpa Mpa) {
        return MpaStorage.addMpa(Mpa);
    }

    public void remove(Long id) {
        MpaStorage.removeMpa(id);
    }

    public List<Mpa> findAll() {
        return MpaStorage.findAll();
    }
}
