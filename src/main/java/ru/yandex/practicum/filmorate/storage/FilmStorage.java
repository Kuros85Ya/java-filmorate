package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {
    Film save(Film film);

    void update(Film film);

    HashMap<Long, Film> getFilms();

    Film getFilm(Long id);
}
