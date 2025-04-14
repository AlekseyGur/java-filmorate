package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.dal.dto.PairIdsDto;
import ru.yandex.practicum.filmorate.dal.mapper.PairIdsDtoRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseRepository<T> {
    private final JdbcOperations jdbc;
    private final RowMapper<T> mapper;
    private final NamedParameterJdbcTemplate njdbc;

    @Autowired
    public BaseRepository(NamedParameterJdbcTemplate njdbc, RowMapper<T> mapper) {
        this.njdbc = njdbc;
        this.mapper = mapper;
        jdbc = njdbc.getJdbcOperations();
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<T> findMany(String query, Object... params) {
        return njdbc.getJdbcOperations().query(query, mapper, params);
    }

    public List<T> findManySqlParameterSource(String query, SqlParameterSource params) {
        return njdbc.query(query, params, mapper);
    }

    public List<Long> findManyIds(String query, Object... params) {
        try {
            return jdbc.query(query, (rs, rowNum) -> rs.getLong(1), params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении ID: " + e.getMessage(), e);
        }
    }

    public boolean unsafeCheckTableContainsId(String tableName, long id) {
        String q = "SELECT id FROM " + tableName + " WHERE id = ? LIMIT 1;";

        try {
            Long count = jdbc.queryForObject(q, Long.class, id);
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        jdbc.update(query, params);
    }

    public List<PairIdsDto> findManyIdToId(String query, SqlParameterSource params) {
        final RowMapper<PairIdsDto> pairMapper = new PairIdsDtoRowMapper();
        try {
            return njdbc.query(query, params, pairMapper);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении IdToId: " + e.getMessage(), e);
        }
    }

    protected void insertMany(String query, SqlParameterSource... params) {
        try {
            njdbc.batchUpdate(query, params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка добавления множества данных: " + e.getMessage(), e);
        }
    }

    protected Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        List<Map<String, Object>> keys = keyHolder.getKeyList();
        int countKeys = keys.size();

        if (countKeys > 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        } else if (countKeys == 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        }
        throw new InternalServerException("Не удалось сохранить данные");
    }
}
