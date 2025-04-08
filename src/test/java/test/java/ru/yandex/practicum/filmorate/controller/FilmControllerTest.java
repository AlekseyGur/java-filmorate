package test.java.ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
@Transactional
public class FilmControllerTest {
    private FilmController filmController;
    @Autowired
    private DirectorController directorController;
    @Autowired
    private UserController userController;

    @Autowired
    public FilmControllerTest(FilmController filmController, UserController userController) {
        this.filmController = filmController;
        this.userController = userController;
    }

    @Test
    void testCreateNormal() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        filmController.create(film);

        Collection<Film> films = filmController.findAll();
        assertTrue(films.size() == 1, "Фильм должен добавиться");
    }

    @Test
    void testUpdateNormal() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        Film createdFilm = filmController.create(film);

        Film film2 = new Film();
        film2.setName("New name");
        film2.setId(createdFilm.getId());
        film2.setDescription("Small descr");
        film2.setDuration(55);
        film2.setReleaseDate(LocalDate.parse("1995-04-21").toString());
        filmController.update(film2);

        Collection<Film> films = filmController.findAll();
        Film firstFilm = films.iterator().next();
        assertTrue(films.size() == 1, "Фильм должен добавиться");
        assertTrue(firstFilm.getName() == "New name", "Фильм должен изменить название");
    }

    @Test
    void testCreateValidErrorBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Small descr");
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> filmController.create(film), "Исключение пустого имени");
    }

    @Test
    void testCreateValidErrorLongDescr() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("a".repeat(Film.MAX_DESCRIPTION_LEN));
        film.setDuration(55);
        film.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("a".repeat(Film.MAX_DESCRIPTION_LEN + 1));
        film2.setDuration(55);
        film2.setReleaseDate(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> filmController.create(film2), "Исключение длинного имени");
    }

    @Test
    void testCreateValidErrorOldReleaseDate() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Descr");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("Descr");
        film2.setDuration(55);
        film2.setReleaseDate(LocalDate.parse(Film.MIN_RELEASE_DATE).minusYears(5).toString());

        assertThrows(ValidationException.class, () -> filmController.create(film2),
                "Дата релиза — не раньше предельного!");
    }

    @Test
    void testCreateValidErrorDuration() {
        Film film = new Film();
        film.setName("Any name");
        film.setDescription("Descr");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertDoesNotThrow(() -> filmController.create(film), "Всё ОК");

        Film film2 = new Film();
        film2.setName("Any name");
        film2.setDescription("Descr");
        film2.setDuration(-1);
        film2.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertThrows(ValidationException.class, () -> filmController.create(film2),
                "Продолжительность должна быть положительной!");

        Film film3 = new Film();
        film3.setName("Any name");
        film3.setDescription("Descr");
        film3.setDuration(0);
        film3.setReleaseDate(Film.MIN_RELEASE_DATE);

        assertThrows(ValidationException.class, () -> filmController.create(film3),
                "Продолжительность должна быть больше нуля!");
    }

    @Test
    void testShouldSearchTitle() {
        Film film = new Film();
        film.setName("Крадущийся тигр, затаившийся дракон");
        film.setDescription("Descr 1");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);

        Film film2 = new Film();
        film2.setName("Крадущийся в ночи");
        film2.setDescription("Descr 2");
        film2.setDuration(11);
        film2.setReleaseDate(Film.MIN_RELEASE_DATE);

        filmController.create(film);
        filmController.create(film2);

        List<Film> films = filmController.findAll();
        assertTrue(films.size() == 2, "Должно быть сохранено два фильма");

        String query = "крад";
        String by = "title";
        List<Film> filmsSearch = filmController.search(query, by);
        assertTrue(filmsSearch.size() == 2, "Должно быть найдено два фильма");

        query = "ТиГ";
        by = "title";
        filmsSearch = filmController.search(query, by);
        assertTrue(filmsSearch.size() == 1, "Должен быть найден один фильм");
    }

    @Test
    void testShouldSearchDirector() {
        Director directorFirst = new Director();
        directorFirst.setName("Director1 name");

        Director directorSecond = new Director();
        directorSecond.setName("Director2 name");

        directorFirst = directorController.add(directorFirst);
        directorSecond = directorController.add(directorSecond);

        Film film = new Film();
        film.setName("Крадущийся тигр, затаившийся дракон");
        film.setDescription("Descr 1");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);
        film.setDirectors(List.of(directorFirst, directorSecond));

        Film film2 = new Film();
        film2.setName("Крадущийся в ночи");
        film2.setDescription("Descr 2");
        film2.setDuration(11);
        film2.setReleaseDate(Film.MIN_RELEASE_DATE);

        filmController.create(film);
        filmController.create(film2);

        List<Film> films = filmController.findAll();
        assertTrue(films.size() == 2, "Должно быть сохранено два фильма");

        String query = "rector2";
        String by = "director";
        List<Film> filmsSearch = filmController.search(query, by);
        assertTrue(filmsSearch.size() == 1, "Должен быть найден один фильм");

        query = "Direct";
        by = "director";
        filmsSearch = filmController.search(query, by);
        assertTrue(filmsSearch.size() == 2, "Должно быть найдено два фильма");
    }

    @Test
    void testShouldSearchDirectorOrTitle() {
        Director directorFirst = new Director();
        directorFirst.setName("Director1 name");

        Director directorSecond = new Director();
        directorSecond.setName("Director2 name");

        directorFirst = directorController.add(directorFirst);
        directorSecond = directorController.add(directorSecond);

        Film film = new Film();
        film.setName("Крадущийся тигр, затаившийся дракон rEctor1");
        film.setDescription("Descr 1");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);
        film.setDirectors(List.of(directorFirst, directorSecond));

        Film film2 = new Film();
        film2.setName("Крадущийся в ночи");
        film2.setDescription("Descr 2");
        film2.setDuration(11);
        film2.setReleaseDate(Film.MIN_RELEASE_DATE);

        filmController.create(film);
        filmController.create(film2);

        List<Film> films = filmController.findAll();
        assertTrue(films.size() == 2, "Должно быть сохранено два фильма");

        String query = "rector2";
        String by = "director,title";
        List<Film> filmsSearch = filmController.search(query, by);
        assertTrue(filmsSearch.size() == 1, "Должен быть найден один фильм");
    }


    @Test
    void getCommonFilmsWithUser() {
        Film film = new Film();
        film.setName("Крадущийся тигр, затаившийся дракон ");
        film.setDescription("Descr 1");
        film.setDuration(55);
        film.setReleaseDate(Film.MIN_RELEASE_DATE);
        film.setId(1L);

        Director directorFirst = new Director();
        directorFirst.setName("Director1 name");

        directorFirst = directorController.add(directorFirst);

        film.setDirectors(List.of(directorFirst));

        filmController.create(film);

        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.parse("1995-04-21").toString());

        userController.addUser(user);

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setLogin("secondLogin");
        secondUser.setName("secondName");
        secondUser.setEmail("secondTest@test.test");
        secondUser.setBirthday(LocalDate.parse("1995-04-22").toString());

        userController.addUser(secondUser);

        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film.getId(), secondUser.getId());

        assertEquals(film, filmController.findCommonFilmsWithFriend(user.getId(), secondUser.getId()).get(0));
    }
}
