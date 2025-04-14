package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.utils.Validate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<Feed> findByUser(@PathVariable Long id) {
        return feedService.findByUser(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll() {
        return userService.findAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public User addUser(@Valid @RequestBody User user) {
        Validate.user(user);
        return userService.addUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        Validate.user(user);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendations(@PathVariable Long id) {
        return userService.getRecommendations(id);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}