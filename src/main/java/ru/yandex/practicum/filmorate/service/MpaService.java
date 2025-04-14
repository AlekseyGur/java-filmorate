package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa get(Long id) {
        return mpaStorage.getMpa(id);
    }

    public Mpa create(Mpa mpa) {
        return mpaStorage.addMpa(mpa);
    }

    public void remove(Long id) {
        mpaStorage.removeMpa(id);
    }

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public HashMap<Long, Mpa> findAllHashMap() {
        HashMap<Long, Mpa> res = new HashMap<>();
        for (Mpa m : mpaStorage.findAll()) {
            res.put(m.getId(), m);
        }
        return res;
    }
}
