package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ToolsDb {
    @Autowired
    private NamedParameterJdbcTemplate njdbc;

    public boolean unsafeCheckTableContainsId(String tableName, Long id) {
        if (id == null) {
            return false;
        }

        String q = "SELECT COUNT(id) FROM " + tableName + " WHERE id = :id LIMIT 1;";
        SqlParameterSource param = new MapSqlParameterSource("id", id);

        Long recs = getCount(q, param);

        return recs > 0;
    }

    public boolean unsafeCheckTableContainsId(String tableName, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        String q = "SELECT COUNT(*) FROM " + tableName + " WHERE id IN (:ids);";
        SqlParameterSource param = new MapSqlParameterSource("ids", ids);

        Long recs = getCount(q, param);

        return recs == ids.size();
    }

    private Long getCount(String query, SqlParameterSource params) {
        try {
            Long count = njdbc.queryForObject(query, params, Long.class);
            return count;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
