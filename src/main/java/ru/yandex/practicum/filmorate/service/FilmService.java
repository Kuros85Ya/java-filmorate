package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmService {
    void addLike(User initiator, Film acceptor);

    void removeLike(User initiator, Film acceptor);

    List<Film> getMostPopularMovies(int numberOfMovies);

    List<Genre> getAllGenres();
    List<Genre> getFilmGenres(Long id);

    Rating getFilmAgeRating(Long id);

    List<Rating> getAllAgeRatings();
}
