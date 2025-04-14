package ru.yandex.practicum.filmorate.dal.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.FeedDto;
import ru.yandex.practicum.filmorate.dal.mapper.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

@Slf4j
@Component
@Repository
public class FeedDbStorage extends BaseRepository<FeedDto> implements FeedStorage {
    private static final String ADD = "INSERT INTO feed(user_id, entity_id, event_type, operation) VALUES (?, ?, ?, ?);";
    private static final String GET_BY_ID = "SELECT * FROM feed WHERE id = ?;";
    private static final String GET_ALL_BY_USER = "SELECT * FROM feed WHERE user_id = ?;";

    @Autowired
    public FeedDbStorage(NamedParameterJdbcTemplate njdbc, FeedRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public Optional<FeedDto> add(Feed feed) {
        long id = insert(ADD,
                feed.getUserId(),
                feed.getEntityId(),
                feed.getEventType().ordinal(),
                feed.getOperation().ordinal());

        return Optional.ofNullable(findOne(GET_BY_ID, id).orElse(null));
    }

    @Override
    public List<FeedDto> findByUser(Long userId) {
        return findMany(GET_ALL_BY_USER, userId);
    }
}