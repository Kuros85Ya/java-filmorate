package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService service;

    @Autowired
    public FilmController(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                          @Qualifier("UserDbStorage") UserStorage userStorage,
                          @Qualifier("filmDbService") FilmService service) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.service = service;
    }

    /**
     * Получение всех фильмов.
     */
    @GetMapping
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    /**
     * Получение фильма.
     */
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("Запрошен фильм + " + id);
        return filmStorage.getFilm(id);
    }

    /**
     * Получение самых популярных по лайкам фильмов.
     */
    @GetMapping("/popular")
    public List<Film> getMostLikedMovies(@RequestParam(defaultValue = "10") String count) {
        log.info("Запрошены самые популярные фильмы");
        return service.getMostPopularMovies(Integer.parseInt(count));
    }

    /**
     * Добавление фильма
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Пытаемся добавить фильм {}", film);
        Film newFilm = filmStorage.save(film);
        log.info("Успешно добавлен {}", film);
        return newFilm;
    }

    /**
     * Обновление фильма
     */
    @SneakyThrows
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        ValidateService.validate(film);
        Film finalFilm = filmStorage.update(film);
        log.info("Изменен фильм с id {}", film.getId());
        return finalFilm;
    }

    /**
     * Добавление лайка к фильму
     */
    @PutMapping(value = "/{acceptorId}/like/{initiatorId}")
    public void likeFilm(@PathVariable Long acceptorId, @PathVariable Long initiatorId) {
        service.addLike(userStorage.getUser(initiatorId), filmStorage.getFilm(acceptorId));
        log.info("Юзер " + initiatorId + " лайкнул фильм " + acceptorId);
    }

    /**
     * Удаление лайка у фильма
     */
    @DeleteMapping(value = "/{acceptorId}/like/{initiatorId}")
    public void removeLike(@PathVariable Long acceptorId, @PathVariable Long initiatorId) {
        service.removeLike(userStorage.getUser(initiatorId), filmStorage.getFilm(acceptorId));
        log.info("Юзер " + initiatorId + " убрал лайк у фильма " + acceptorId);
    }
}
