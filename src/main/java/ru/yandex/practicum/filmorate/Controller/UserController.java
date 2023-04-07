package ru.yandex.practicum.filmorate.Controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateData;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private int id = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    /**
     * Получение всех фильмов.
     */
    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Текущее количество фильмов: {}", users.size());
        return users.values().stream().toList();
    }

    /**
     * Добавление фильма
     */
    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Пытаемся добавить юзера {}", user);
        User validatedUser = ValidateData.validate(user);
        user.setId(id);
        users.put(id, validatedUser);
        id += 1;
        log.info("Успешно добавлен {}", user);
        return user;
    }

    /**
     * Обновление фильма
     */
    @PutMapping(value = "/users")
    public User updateFilm(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователя с id " + id + " не существует");
        User validatedUser = ValidateData.validate(user);
        users.put(user.getId(), validatedUser);
        log.info("Изменен юзер с id {}", user.getId());
        return user;
    }
}
