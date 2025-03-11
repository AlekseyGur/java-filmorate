package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }
    
    public Mpa get(Long id) {
        return mpaStorage.getMpa(id);
    }
    
    public Mpa create(Mpa Mpa) {
        return mpaStorage.addMpa(Mpa);
    }

    public void remove(Long id) {
        mpaStorage.removeMpa(id);
    }

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }
}
