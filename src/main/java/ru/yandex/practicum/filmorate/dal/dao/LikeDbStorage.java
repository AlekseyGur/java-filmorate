package ru.yandex.practicum.filmorate.dal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.PairIdsDto;
import ru.yandex.practicum.filmorate.dal.mapper.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class LikeDbStorage extends BaseRepository<Like> implements LikeStorage {
    private static final String LIKE_ADD = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
    private static final String LIKE_DELETE = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ? LIMIT 1;";
    private static final String LIKE_GET_FILMS_IDS = "SELECT film_id, user_id FROM films_likes WHERE film_id IN (:ids);";
    private static final String LIKE_CHECK_EXIST = "SELECT * FROM films_likes WHERE film_id = ? AND user_id = ?;";

    @Autowired
    public LikeDbStorage(NamedParameterJdbcTemplate njdbc, LikeRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        insert(LIKE_ADD, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        update(LIKE_DELETE, filmId, userId);
    }

    @Override
    public boolean checkLikeExist(Long filmId, Long userId) {
        return findOne(LIKE_CHECK_EXIST, filmId, userId).isPresent();
    }

    @Override
    public HashMap<Long, List<Long>> findAllByFilmIds(List<Long> filmsIds) {
        HashMap<Long, List<Long>> res = new HashMap<>();
        SqlParameterSource parameters = new MapSqlParameterSource("ids", filmsIds);
        for (PairIdsDto row : findManyIdToId(LIKE_GET_FILMS_IDS, parameters)) {
            Long filmId = row.getFirst();
            Long userId = row.getLast();
            res.computeIfAbsent(filmId, k -> new ArrayList<>()).add(userId);
        }
        return res;
    }
}
