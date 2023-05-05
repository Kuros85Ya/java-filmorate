package ru.yandex.practicum.filmorate.storage;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.HashMap;
import java.util.NoSuchElementException;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();

    private long generatorId;

    public long generateId() {
        return ++generatorId;
    }

    @Override
    public void save(Film film) {
        ValidateService.validate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
    }

    @Override
    @SneakyThrows
    public void update(Film film) {
        if (!films.containsKey(film.getId()))
            throw new NoSuchElementException("Фильма с id " + film.getId() + " не существует");
        films.put(film.getId(), film);
    }

    @Override
    public HashMap<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilm(Long id) {
        if (films.get(id) == null) throw new NoSuchElementException("Фильма с id " + id + "не существует");
        return films.get(id);
    }
}
