package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    FilmRepository repository = new FilmRepository();

    /**
     * Получение всех фильмов.
     */
    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", repository.getFilms().size());
        return new ArrayList<>(repository.getFilms().values());
    }

    /**
     * Добавление фильма
     */
    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Пытаемся добавить фильм {}", film);
        repository.save(film);
        log.info("Успешно добавлен {}", film);
        return film;
    }

    /**
     * Обновление фильма
     */
    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!repository.getFilms().containsKey(film.getId())) throw new ValidationException("Фильма с id " + film.getId() + " не существует");
        ValidateService.validate(film);
        repository.update(film);
        log.info("Изменен фильм с id {}", film.getId());
        return film;
    }
}
