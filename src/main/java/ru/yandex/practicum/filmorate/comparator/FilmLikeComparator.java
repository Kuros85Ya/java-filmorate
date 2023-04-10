package ru.yandex.practicum.filmorate.comparator;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikeComparator implements Comparator<Film> {
    @Override
    public int compare(Film item1, Film item2) {
        return Integer.compare(item1.getUserLiked().size(), item2.getUserLiked().size());
    }
}
