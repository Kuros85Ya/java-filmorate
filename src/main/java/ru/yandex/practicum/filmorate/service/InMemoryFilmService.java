package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.comparator.FilmLikeComparator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(User initiator, Film acceptor) {
        Set<Long> userLikes = initiator.getLikedMovies();
        Set<Long> filmLikes = acceptor.getUserLiked();

        userLikes.add(acceptor.getId());
        filmLikes.add(initiator.getId());

        initiator.setLikedMovies(filmLikes);
        acceptor.setUserLiked(userLikes);
    }

    public void removeLike(User initiator, Film acceptor) {
        Set<Long> userLikes = initiator.getLikedMovies();
        Set<Long> filmLikes = acceptor.getUserLiked();

        userLikes.remove(acceptor.getId());
        filmLikes.remove(initiator.getId());

        initiator.setLikedMovies(filmLikes);
        acceptor.setUserLiked(userLikes);
    }

    public List<Film> getMostPopularMovies(int numberOfMovies) {
        List<Film> allMovies = new ArrayList<>(filmStorage.getFilms().values());
        allMovies.sort(new FilmLikeComparator());
        return allMovies.stream().limit(numberOfMovies).collect(Collectors.toList());
    }

    @Override
    public List<Genre> getAllGenres() {
        throw new UnsupportedOperationException("Реализация метода существует только при работе с БД");
    }

    @Override
    public List<Genre> getFilmGenres(Long id) {
        throw new UnsupportedOperationException("Реализация метода существует только при работе с БД");
    }

    @Override
    public Rating getFilmAgeRating(Long id) {
        throw new UnsupportedOperationException("Реализация метода существует только при работе с БД");
    }

    @Override
    public List<Rating> getAllAgeRatings() {
        throw new UnsupportedOperationException("Реализация метода существует только при работе с БД");
    }
}
