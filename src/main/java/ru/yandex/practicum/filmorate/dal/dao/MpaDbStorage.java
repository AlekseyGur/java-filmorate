package ru.yandex.practicum.filmorate.dal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String MPA_ADD = "INSERT INTO Mpa(name) VALUES (?);";
    private static final String MPA_GET_BY_ID = "SELECT * FROM Mpa WHERE id = ?;";
    private static final String MPA_GET_ALL = "SELECT * FROM Mpa ORDER BY id ASC;";
    private static final String MPA_DELETE = "DELETE FROM Mpa WHERE id = ? LIMIT 1;";

    @Autowired
    public MpaDbStorage(NamedParameterJdbcTemplate njdbc, MpaRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public Mpa addMpa(Mpa mpa) {
        Long id = insert(MPA_ADD, mpa.getName());
        return findOne(MPA_GET_BY_ID, id).get();
    }

    @Override
    public void removeMpa(Long id) {
        update(MPA_DELETE, id);
    }

    @Override
    public Mpa getMpa(Long id) {
        if (!unsafeCheckTableContainsId("mpa", id)) {
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        }
        return findOne(MPA_GET_BY_ID, id).get();
    }

    @Override
    public List<Mpa> findAll() {
        return findMany(MPA_GET_ALL);
    }
}