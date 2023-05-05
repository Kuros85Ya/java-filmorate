package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class FilmorateMapper {
    public Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(Rating.builder().id(resultSet.getLong("rating_id")).name(resultSet.getString("rating_name")).build())
                .genres(new ArrayList<>())
                .build();
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public List<Film> mapRowToFilmsWithGenres(ResultSet resultSet, int rowNum, List<Film> films) throws SQLException {
        Long filmId = resultSet.getLong("film_id");

        Optional<Film> film = films.stream()
                .filter(it -> it.getId().equals(filmId))
                .findFirst();

        if (film.isPresent()) {
            List<Genre> genres;
            if (film.get().getGenres() != null) {
                genres = film.get().getGenres();
            } else genres = new ArrayList<>();
            genres.add(Genre.builder().id(resultSet.getLong("genre_id")).name(resultSet.getString("genre_name")).build());
            film.get().setGenres(genres);
        }
        return null;
    }

    public Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
