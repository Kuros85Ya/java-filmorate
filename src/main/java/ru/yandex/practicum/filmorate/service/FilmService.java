package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.comparator.FilmLikeComparator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
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
}
