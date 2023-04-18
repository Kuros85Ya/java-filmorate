package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserStorage repository;
    private final UserService service;

    @Autowired
    public UserController(@Qualifier("UserDbStorage") UserStorage storage, @Qualifier("userDbService") UserService service) {
        this.repository = storage;
        this.service = service;
    }

    /**
     * Получение пользователя по id
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Запрошен пользователь " + id);
        return repository.getUser(id);
    }

    /**
     * Получение всех друзей.
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Запрошен список друзей пользователя {}", id);
        User initiator =  repository.getUser(id);
        return service.getFriends(initiator);
    }

    /**
     * Получение списка общих друзей
     */
    @GetMapping(value = "/{initiatorId}/friends/common/{acceptorId}")
    public List<User> getCommonFriends(@PathVariable Long initiatorId, @PathVariable Long acceptorId) {
        log.info("Запрошены общие друзья пользователей " + initiatorId + " и " + acceptorId);
        return service.getCommonFriends(repository.getUser(initiatorId), repository.getUser(acceptorId));
    }

    /**
     * Получение всех пользователей.
     */
    @GetMapping
    public List<User> findAll() {
        log.info("Текущее количество фильмов: {}", repository.getUsers().size());
        return new ArrayList<>(repository.getUsers().values());
    }

    /**
     * Добавление пользователя
     */
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Пытаемся добавить юзера {}", user);
        User addedUser = repository.save(user);
        log.info("Успешно добавлен {}", user);
        return addedUser;
    }

    /**
     * Обновление пользователя
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        repository.update(user);
        log.info("Изменен юзер с id {}", user.getId());
        return user;
    }

    /**
     * Добавление в друзья
     */
    @PutMapping(value = "/{initiatorId}/friends/{acceptorId}")
    public void addUserAsFriend(@PathVariable Long initiatorId, @PathVariable Long acceptorId) {
        service.addFriend(repository.getUser(initiatorId), repository.getUser(acceptorId));
        log.info("Добавились в друзья пользователи " + initiatorId + " и " + acceptorId);
    }

    /**
     * Удаление из друзей
     */
    @DeleteMapping(value = "/{initiatorId}/friends/{acceptorId}")
    public void removeUserFromFriends(@PathVariable Long initiatorId, @PathVariable Long acceptorId) {
        service.removeFriend(repository.getUser(initiatorId), repository.getUser(acceptorId));
        log.info("Удалились из друзей пользователи " + initiatorId + " и " + acceptorId);
    }
}
