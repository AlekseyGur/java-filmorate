package test.java.ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest(classes = FilmorateApplication.class)
@Transactional
public class ReviewControllerTest {
    @Autowired
    private ReviewController reviewController;
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    private User userFirst;
    private User userSecond;

    private Film film;

    private Review reviewPositive;
    private Review reviewNegative;

    @BeforeEach
    void setup() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());
        this.film = filmController.create(film);

        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.parse("1995-04-21").toString());
        this.userFirst = userController.addUser(user);

        user = new User();
        user.setLogin("Otherlogin");
        user.setName("Other name");
        user.setEmail("other_est@test.test");
        user.setBirthday(LocalDate.parse("2011-03-12").toString());
        userSecond = userController.addUser(user);

        Review reviewNegative = new Review();
        reviewNegative.setFilmId(this.film.getId());
        reviewNegative.setUserId(this.userFirst.getId());
        reviewNegative.setContent("Текст негативного отзыва");
        reviewNegative.setIsPositive(false);
        this.reviewNegative = reviewNegative;

        Review reviewPositive = new Review();
        reviewPositive.setFilmId(this.film.getId());
        reviewPositive.setUserId(this.userFirst.getId());
        reviewPositive.setContent("Текст позитивного отзыва");
        reviewPositive.setIsPositive(true);
        this.reviewPositive = reviewPositive;
    }

    @Test
    void shouldAdd() {
        Review reviewSaved = reviewController.add(reviewNegative);
        assertTrue(reviewSaved.equals(reviewNegative), "Отзыв на фильм должен добавиться");
    }

    @Test
    void shouldUpdate() {
        Review reviewSaved = reviewController.add(reviewNegative);
        reviewSaved.setIsPositive(true);
        Review reviewSavedPositive = reviewController.update(reviewSaved);
        assertTrue(reviewSavedPositive.getIsPositive() == true, "Отзыв на фильм должен стать позитивным");
    }

    @Test
    void shouldDelete() {
        Review reviewSaved = reviewController.add(reviewNegative);
        reviewController.delete(reviewSaved.getReviewId());
        Long id = reviewSaved.getReviewId();
        assertThrows(NotFoundException.class, () -> reviewController.get(id));
    }

    @Test
    void shouldGet() {
        Review reviewSaved = reviewController.add(reviewPositive);
        reviewSaved = reviewController.get(reviewSaved.getReviewId());
        assertTrue(reviewSaved.equals(reviewPositive), "Отзыв на фильм должен возвращаться неизменным");
    }

    @Test
    void shouldGetFilmReviews() {
        reviewController.add(reviewPositive);
        reviewController.add(reviewNegative);

        int count = 10;
        List<Review> rev = reviewController.getFilmReviews(film.getId(), count);

        assertTrue(rev.size() == 2, "Должно быть два отзыва на фильм");
    }

    @Test
    void shouldAddReviewLike() {
        Review review = reviewController.add(reviewPositive);
        reviewController.addLike(review.getReviewId(), userFirst.getId());

        int count = 10;
        List<Review> revs = reviewController.getFilmReviews(film.getId(), count);

        Review rev = revs.get(0);
        assertTrue(rev.getUseful() == 1, "Рейтинг должен увеличиваться");
    }

    @Test
    void shouldAddReviewDislike() {
        Review review = reviewController.add(reviewNegative);
        reviewController.addDislike(review.getReviewId(), userFirst.getId());

        int count = 10;
        List<Review> revs = reviewController.getFilmReviews(film.getId(), count);

        Review rev = revs.get(0);
        assertTrue(rev.getUseful() == -1, "Рейтинг должен уменьшиться");
    }

    @Test
    void shouldRemoveReviewLike() {
        Review review = reviewController.add(reviewPositive);
        reviewController.addLike(review.getReviewId(), userFirst.getId());
        reviewController.removeLike(review.getReviewId(), userFirst.getId());

        int count = 10;
        List<Review> revs = reviewController.getFilmReviews(film.getId(), count);

        Review rev = revs.get(0);
        assertTrue(rev.getUseful() == 0, "Рейтинг не должен изменяться (равен 0, если никто не голосовал)");
    }

    @Test
    void shouldRemoveReviewDislike() {
        Review review = reviewController.add(reviewPositive);
        reviewController.addLike(review.getReviewId(), userFirst.getId());
        reviewController.addDislike(review.getReviewId(), userSecond.getId());
        reviewController.removeDislike(review.getReviewId(), userFirst.getId());

        int count = 10;
        List<Review> revs = reviewController.getFilmReviews(film.getId(), count);

        Review rev = revs.get(0);
        assertTrue(rev.getUseful() == 0, "Рейтинг должен изменяться");
    }

}
