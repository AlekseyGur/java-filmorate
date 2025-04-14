package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.utils.Validate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director add(@Valid @RequestBody Director director) {
        Validate.director(director);
        return directorService.add(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@Valid @RequestBody Director director) {
        Validate.director(director);
        return directorService.update(director);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director get(@PathVariable Long id) {
        return directorService.get(id);
    }
}