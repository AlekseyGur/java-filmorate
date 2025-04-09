package test.java.ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperation;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;

@SpringBootTest(classes = FilmorateApplication.class)
@Transactional
public class FeedControllerTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserController userController;
    @Autowired
    private FilmController filmController;
    @Autowired
    private ReviewController reviewController;

    private User user;
    private Film film;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.parse("1995-04-21").toString());
        this.user = userController.addUser(user);

        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());
        this.film = filmController.create(film);
    }

    @Test
    void testAdd() {
        feedService.add(
                user.getId(),
                film.getId(),
                FeedEventType.REVIEW,
                FeedOperation.ADD);

        List<Feed> feeds = userController.findByUser(user.getId());

        assertTrue(feeds.size() == 1, "Запись должна добавиться");
    }

    @Test
    void testReviewAdd() {
        Review reviewNegative = new Review();
        reviewNegative.setFilmId(this.film.getId());
        reviewNegative.setUserId(this.user.getId());
        reviewNegative.setContent("Текст негативного отзыва");
        reviewNegative.setIsPositive(false);
        reviewController.add(reviewNegative);

        List<Feed> feeds = userController.findByUser(user.getId());

        assertTrue(feeds.size() == 1, "Запись должна добавиться");
    }
}
