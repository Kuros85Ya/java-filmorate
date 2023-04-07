package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private int id = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    /**
     * Получение всех фильмов.
     */
    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return (List<Film>) films.values();
    }

    /**
     * Добавление фильма
     */
    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Пытаемся добавить фильм {}", film);
        ValidateService.validate(film);
        film.setId(id);
        films.put(id, film);
        id += 1;
        log.info("Успешно добавлен {}", film);
        return film;
    }

    /**
     * Обновление фильма
     */
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) throw new ValidationException("Фильма с id " + id + " не существует");
        ValidateService.validate(film);
        films.put(film.getId(), film);
        log.info("Изменен фильм с id {}", film.getId());
        return film;
    }
}
