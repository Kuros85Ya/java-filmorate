package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService service;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage, FilmService service) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.service = service;
    }

    /**
     * Получение всех фильмов.
     */
    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    /**
     * Получение самых популярных по лайкам фильмов.
     */
    @GetMapping("/films/popular?count={count}")
    public List<Film> getMostLikedMovies(@PathVariable int count) {
        log.info("Запрошены самые популярные фильмы");
        return service.getMostPopularMovies(count);
    }

    /**
     * Добавление фильма
     */
    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Пытаемся добавить фильм {}", film);
        filmStorage.save(film);
        log.info("Успешно добавлен {}", film);
        return film;
    }

    /**
     * Обновление фильма
     */
    @SneakyThrows
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!filmStorage.getFilms().containsKey(film.getId()))
            throw new ValidationException("Фильма с id " + film.getId() + " не существует");
        ValidateService.validate(film);
        filmStorage.update(film);
        log.info("Изменен фильм с id {}", film.getId());
        return film;
    }

    /**
     * Добавление лайка к фильму
     */
    @PutMapping(value = "/films/{acceptorId}/like/{initiatorId}")
    public void likeFilm(@PathVariable Long acceptorId, @PathVariable Long initiatorId) {
        service.addLike(userStorage.getUser(initiatorId), filmStorage.getFilm(acceptorId));
        log.info("Юзер " + initiatorId + " лайкнул фильм " + acceptorId);
        //if (!repository.getFilms().containsKey(film.getId())) throw new ValidationException("Фильма с id " + film.getId() + " не существует");
        //ValidateService.validate(film);
        //repository.update(film);
    }

    /**
     * Удаление лайка у фильма
     */
    @DeleteMapping(value = "/films/{acceptorId}/like/{initiatorId}")
    public void removeLike(@PathVariable Long acceptorId, @PathVariable Long initiatorId) {
        service.removeLike(userStorage.getUser(initiatorId), filmStorage.getFilm(acceptorId));
        log.info("Юзер " + initiatorId + " убрал лайк у фильма " + acceptorId);
        //if (!repository.getFilms().containsKey(film.getId())) throw new ValidationException("Фильма с id " + film.getId() + " не существует");
        //ValidateService.validate(film);
        //repository.update(film);
    }
}
