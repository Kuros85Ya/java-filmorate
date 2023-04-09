package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.HashMap;

@Component
public class FilmRepository {
    private final HashMap<Long, Film> films = new HashMap<>();

    private long generatorId;

    public long generateId() {
        return ++generatorId;
    }

    public void save(Film film) {
        ValidateService.validate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
    }

    public void update(Film film) {
        if (!films.containsKey(film.getId()))
            throw new ValidationException("Фильма с id " + film.getId() + " не существует");
        films.put(film.getId(), film);
    }

    public HashMap<Long, Film> getFilms() {
        return films;
    }
}
