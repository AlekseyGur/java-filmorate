package ru.yandex.practicum.filmorate.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "login" })
public class User {
    private Long id;

    @Email
    private String email;

    @NotBlank
    private String login;
    private String name;
    private String birthday;

    private Set<Long> friends = new HashSet<>();

    public void setFriends(Set<Long> friends) {
        this.friends = friends;
    }

    public void addFriends(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriends(Long friendId) {
        friends.remove(friendId);
    }

    public Set<Long> getFriends() {
        return friends;
    }
}