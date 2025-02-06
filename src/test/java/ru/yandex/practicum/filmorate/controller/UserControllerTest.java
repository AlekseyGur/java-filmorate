package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserControllerTest {
    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testCreateNormal() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.parse("1995-04-21").toString());

        userController.create(user);

        Collection<User> users = userController.findAll();
        assertTrue(users.size() == 1, "Пользователь должен добавиться");
    }

    @Test
    void testCreateNoNameSetLoginToName() {
        User user = new User();
        user.setLogin("login");
        // user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.parse("1995-04-21").toString());

        userController.create(user);

        Collection<User> users = userController.findAll();
        User firstUser = users.iterator().next();
        assertTrue(firstUser.getName().equals("login"), "Пользователь должен добавиться");
    }

    @Test
    void testUpdateNormal() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.now().toString());

        userController.create(user);

        User user2 = new User();
        user2.setLogin("newlogin");
        user2.setName("name");
        user2.setId(1L);
        user2.setEmail("test@test.test");
        user2.setBirthday(LocalDate.now().minusYears(28).toString());
        userController.update(user2);

        Collection<User> users = userController.findAll();
        User firstUser = users.iterator().next();
        assertTrue(users.size() == 1, "Пользователь должен добавиться");
        assertTrue(firstUser.getLogin() == "newlogin", "Пользователь должен изменить login");
    }

    @Test
    void testUpdateErrorUnknownUser() {
        User user = new User();
        user.setId(5L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.now().toString());

        assertThrows(NotFoundException.class, () -> userController.update(user), "Нельзя изменить несуществующего пользователя");
    }

    @Test
    void testCreateValidErrorBlankLogin() {
        User user = new User();
        user.setLogin("");
        user.setName("Small descr");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.now().minusYears(28).toString());

        assertThrows(ValidationException.class, () -> userController.create(user), "Исключение пустого Login");
    }

    @Test
    void testCreateValidErrorEmail() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.now().minusYears(28).toString());

        assertDoesNotThrow(() -> userController.create(user), "Всё ОК");

        User user2 = new User();
        user2.setLogin("login");
        user2.setName("name");
        user2.setEmail("test_test.test");
        user2.setBirthday(LocalDate.now().minusYears(28).toString());

        assertThrows(ValidationException.class, () -> userController.create(user2),
                "Электронная почта должна содержать символ @");

        User user3 = new User();
        user3.setLogin("login");
        user3.setName("name");
        user3.setEmail("");
        user3.setBirthday(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> userController.create(user3),
                "Электронная почта не может быть пустой");

        User user4 = new User();
        user4.setLogin("login");
        user4.setName("name");
        user4.setEmail("это-неправильный?эмейл@");
        user4.setBirthday(LocalDate.parse("1995-04-21").toString());

        assertThrows(ValidationException.class, () -> userController.create(user4),
                "Электронная почта должна проверяться");
    }

    @Test
    void testCreateValidErrorBirthdayBegoreNow() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("test@test.test");
        user.setBirthday(LocalDate.now().toString());

        assertDoesNotThrow(() -> userController.create(user), "Всё ОК");

        User user2 = new User();
        user2.setLogin("Any name");
        user2.setName("Descr");
        user2.setEmail("test@test.test");
        user2.setBirthday(LocalDate.now().plusMonths(4).toString());

        assertThrows(ValidationException.class, () -> userController.create(user2),
                "Дата рождения не может быть в будущем!");
    }

}
