package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.HashMap;

@Component
public class UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();

    private long generatorId;

    public long generateId() {
        return ++generatorId;
    }

    public void save(User user) {
        User validatedUser = ValidateService.validate(user);
        user.setId(generateId());
        users.put(user.getId(), validatedUser);
    }

    public void update(User user) {
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Юзера с id " + user.getId() + " не существует");
        User validatedUser = ValidateService.validate(user);
        users.put(validatedUser.getId(), user);
    }

    public HashMap<Long, User> getUsers() {
        return users;
    }
}
