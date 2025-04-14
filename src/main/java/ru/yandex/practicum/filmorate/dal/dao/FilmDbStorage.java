package ru.yandex.practicum.filmorate.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@Repository
public class FilmDbStorage extends BaseRepository<FilmDto> implements FilmStorage {

    private static final String FILM_ADD = "INSERT INTO films(name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?);";
    private static final String FILM_GET_BY_ID = "SELECT * FROM films WHERE id = ?;";
    private static final String FILMS_GET_BY_IDS = "SELECT * FROM films WHERE id IN (?);";
    private static final String FILM_GET_ALL = "SELECT * FROM films;";
    private static final String FILM_UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id = ? LIMIT 1;";
    private static final String FILM_DELETE = "DELETE FROM films WHERE id = ?;";
    private static final String GET_SORTED_POPULAR_FILMS = """
            SELECT f.*, COUNT(fl.user_id) AS likes_count
            FROM films AS f
                    LEFT JOIN films_likes AS fl ON f.id = fl.film_id
                    LEFT JOIN films_genres AS fg ON f.id = fg.film_id
    """;

    private static final String GET_COMMON_FILMS_WITH_FRIEND = "SELECT f.* " +
            "FROM films f " +
            "WHERE f.id IN (" +
            "    SELECT l.film_id" +
            "    FROM films_likes l " +
            "    JOIN films_likes fl ON l.film_id = fl.film_id " +
            "    WHERE l.user_id = ? AND fl.user_id = ?" +
            "    GROUP BY l.film_id" +
            "    ORDER BY COUNT(*) DESC" +
            ")";

    private static final String FILMS_GET_BY_DIRECTOR_SORT_YEAR = """
            SELECT f.*
            FROM films f
            LEFT JOIN films_directors d ON f.id = d.film_id
            WHERE d.director_id = ?
            GROUP BY f.id
            ORDER BY YEAR(f.release_date) ASC;""";
    private static final String FILMS_GET_BY_DIRECTOR_SORT_LIKES = """
            SELECT f.*
            FROM films f
            LEFT JOIN films_directors d ON f.id = d.film_id
            LEFT JOIN films_likes l ON f.id = l.film_id
            WHERE d.director_id = ?
            GROUP BY f.id
            ORDER BY COUNT(l.*) DESC;""";

    private static final String FILMS_GET_BY_TITLE = "SELECT * FROM films WHERE LOWER(name) LIKE ?;";
    private static final String FILMS_GET_BY_DIRECTOR_NAME = """
            SELECT f.*
            FROM films f
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            WHERE LOWER(d.name) LIKE ?;""";
    private static final String FILMS_GET_BY_TITLE_OR_BY_DIRECTOR_NAME = """
            SELECT f.*
            FROM films f
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            WHERE LOWER(d.name) LIKE ? OR LOWER(f.name) LIKE ?
            ORDER BY f.id DESC;""";

    private static final String FILMS_GET_RECOMMENDATIONS = """
            WITH most_similar_user AS (
                SELECT fl2.user_id
                FROM films_likes fl1
                JOIN films_likes fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ?
                    AND fl2.user_id != ?
                GROUP BY fl2.user_id
                ORDER BY COUNT(fl2.film_id) DESC
                LIMIT 1
            )

            SELECT f.*
            FROM films f
            JOIN films_likes fl ON f.id = fl.film_id
            WHERE fl.user_id = (SELECT user_id FROM most_similar_user)
                AND f.id NOT IN (
                    SELECT film_id
                    FROM films_likes
                    WHERE user_id = ?
                )
            ORDER BY (
                SELECT COUNT(*)
                FROM films_likes
                WHERE film_id = f.id
            ) DESC;
    """;

    @Autowired
    public FilmDbStorage(NamedParameterJdbcTemplate njdbc, FilmRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public Optional<FilmDto> addFilm(Film film) {
        Long mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();
        Long id = insert(
                FILM_ADD,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId);

        return Optional.ofNullable(getFilmImpl(id).orElse(null));
    }

    @Override
    public Optional<FilmDto> getFilm(Long id) {
        return Optional.ofNullable(getFilmImpl(id).orElse(null));
    }

    @Override
    public List<FilmDto> getRecommendedFilms(Long userId) {
        return findMany(FILMS_GET_RECOMMENDATIONS, userId, userId, userId);
    }

    @Override
    public void deleteFilm(Long id) {
        delete(FILM_DELETE, id);
    }

    @Override
    public List<FilmDto> findAll() {
        return findMany(FILM_GET_ALL);
    }

    @Override
    public List<FilmDto> findAllByDirectorIdSort(Long directorId, String sortBy) {
        if (sortBy.equals("likes")) {
            return findMany(FILMS_GET_BY_DIRECTOR_SORT_LIKES, directorId);
        }
        return findMany(FILMS_GET_BY_DIRECTOR_SORT_YEAR, directorId);
    }

    @Override
    public List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        StringBuilder request = new StringBuilder(GET_SORTED_POPULAR_FILMS);
        List<Object> params = new ArrayList<>();

        if (genreId != null && year != null) {
            request.append(" WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?");
            params.add(genreId);
            params.add(year);
        } else if (genreId != null) {
            request.append(" WHERE fg.genre_id = ?");
            params.add(genreId);
        } else if (year != null) {
            request.append(" WHERE EXTRACT(YEAR FROM f.release_date) = ?");
            params.add(year);
        }

        request.append(" GROUP BY f.id ORDER BY likes_count DESC");

        if (count != null) {
            request.append(" LIMIT ?");
            params.add(count);
        }

        return findMany(request.toString(), params.toArray());
    }

    @Override
    public List<FilmDto> getFilms(List<Long> ids) {
        String q = ids.stream().map(id -> id.toString()).collect(Collectors.joining(","));
        return findMany(FILMS_GET_BY_IDS, q);
    }

    @Override
    public Optional<FilmDto> updateFilm(Film film) {
        Long mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();
        update(FILM_UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId());

        return Optional.ofNullable(getFilmImpl(film.getId()).orElse(null));
    }

    @Override
    public List<FilmDto> searchByTitle(String query) {
        return findMany(FILMS_GET_BY_TITLE, "%" + query + "%");
    }

    @Override
    public List<FilmDto> searchByDirector(String query) {
        return findMany(FILMS_GET_BY_DIRECTOR_NAME, "%" + query + "%");
    }

    @Override
    public List<FilmDto> searchByTitleOrDirector(String query) {
        String q = "%" + query + "%";
        return findMany(FILMS_GET_BY_TITLE_OR_BY_DIRECTOR_NAME, q, q);
    }

    @Override
    public List<FilmDto> getCommonFilmsWithFriend(Long userId, Long friendId) {
        return findMany(GET_COMMON_FILMS_WITH_FRIEND, userId, friendId);
    }

    private Optional<FilmDto> getFilmImpl(Long id) {
        return findOne(FILM_GET_BY_ID, id);
    }
}
