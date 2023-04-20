package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {

    HashMap<Long, User> getUsers();

    void update(User user);

    User save(User user);

    User getUser(Long id);
}
