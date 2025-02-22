package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    @Override
    public User addUser(User user) {
        userId++;
        log.info("Добавлен пользователь " + userId);
        user.setId(userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        user.addFriends(friendId);
        friend.addFriends(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        user.removeFriends(friendId);
        friend.removeFriends(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return findAllByIds(user.getFriends());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = users.get(userId);
        User otherUser = users.get(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (otherUser == null) {
            throw new NotFoundException("Друг с id = " + otherId + " не найден");
        }

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return findAllByIds(commonFriends);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private List<User> findAllByIds(Set<Long> usersIds) {
        return users.values().stream().filter(x -> usersIds.contains(x.getId())).toList();
    }
}
