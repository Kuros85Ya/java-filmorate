package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.HashMap;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    private long generatorId;

    public long generateId() {
        return ++generatorId;
    }

    @Override
    public User save(User user) {
        User validatedUser = ValidateService.validate(user);
        validatedUser.setId(generateId());
        users.put(validatedUser.getId(), validatedUser);
        return validatedUser;
    }

    @Override
    public User getUser(Long id) {
        if (users.get(id) == null) throw new NoSuchElementException("Пользователя с id " + id + " не существует");
        return users.get(id);
    }

    @Override
    public void update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Юзера с id " + user.getId() + " не существует");
        }
        User validatedUser = ValidateService.validate(user);
        users.put(validatedUser.getId(), user);
    }

    @Override
    public HashMap<Long, User> getUsers() {
        return users;
    }
}
