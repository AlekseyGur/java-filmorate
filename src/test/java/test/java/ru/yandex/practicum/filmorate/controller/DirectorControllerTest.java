package test.java.ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.model.Director;

@SpringBootTest(classes = FilmorateApplication.class)
@Transactional
public class DirectorControllerTest {
    @Autowired
    private DirectorController directorController;

    Director directorFirst;
    Director directorSecond;

    @BeforeEach
    void setup() {
        Director directorFirst = new Director();
        directorFirst.setName("Any name");
        this.directorFirst = directorFirst;

        Director directorSecond = new Director();
        directorSecond.setName("Other name");
        this.directorSecond = directorSecond;
    }

    @Test
    void testFindAll() {
        directorController.add(directorFirst);
        directorController.add(directorSecond);

        List<Director> directors = directorController.findAll();

        assertTrue(directors.size() == 2, "Режиссёр должен добавиться");
    }

    @Test
    void testDelete() {
        Director dir = directorController.add(directorFirst);
        directorController.add(directorSecond);

        directorController.delete(dir.getId());

        List<Director> directors = directorController.findAll();

        assertTrue(directors.size() == 1, "Режиссёр должен удалиться");
    }

    @Test
    void testAdd() {
        List<Director> directors = directorController.findAll();

        assertTrue(directors.size() == 0, "Сначала никого нет в списке режиссёров");

        directorController.add(directorFirst);

        directors = directorController.findAll();

        assertTrue(directors.size() == 1, "Должен быть 1 режиссёр");

        Director dir = directors.get(0);

        assertTrue(dir.equals(directorFirst), "Данные должны совпадать");
    }

    @Test
    void testUpdate() {
        directorController.add(directorFirst);
        Director dir = directorController.findAll().get(0);

        dir.setName("New name");
        directorController.update(dir);

        Director dirSaved = directorController.findAll().get(0);

        assertTrue(dirSaved.getName().equals("New name"), "Режисёр должен сменить имя");
    }
}
