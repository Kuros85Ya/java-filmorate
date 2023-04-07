package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    UserRepository repository = new UserRepository();

    /**
     * Получение всех фильмов.
     */
    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Текущее количество фильмов: {}", repository.getUsers().size());
        return new ArrayList<>(repository.getUsers().values());
    }

    /**
     * Добавление фильма
     */
    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Пытаемся добавить юзера {}", user);
        repository.save(user);
        log.info("Успешно добавлен {}", user);
        return user;
    }

    /**
     * Обновление фильма
     */
    @PutMapping(value = "/users")
    public User updateFilm(@Valid @RequestBody User user) {
        repository.update(user);
        log.info("Изменен юзер с id {}", user.getId());
        return user;
    }
}
