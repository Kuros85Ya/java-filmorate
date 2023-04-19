package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    HashMap<Long, Film> getFilms();

    void setFilmGenres(List<Film> films);

    Film getFilm(Long id);
}
