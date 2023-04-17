package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(User initiator, Film acceptor) {
        String sqlQuery = "insert into USER_FILM_LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());
    }

    @Override
    public void removeLike(User initiator, Film acceptor) {
        String sqlQuery = "delete from USER_FILM_LIKES where USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());
    }

    @Override
    public List<Film> getMostPopularMovies(int numberOfMovies) {

        String sqlQuery = "SELECT ID, NAME, DESCRIPTION, DURATION, RELEASE_DATE" +
                " from FILM WHERE ID IN (SELECT FILM_ID FROM " +
                "(SELECT FILM_ID, COUNT(*) FROM USER_FILM_LIKES " +
                "GROUP BY FILM_ID " +
                "ORDER BY 2 DESC " +
                "LIMIT 10))";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Genre> getFilmGenres(Long id) {
        String sqlQuery = "select ID, NAME from GENRE WHERE ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select ID, NAME from GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Rating getFilmAgeRating(Long id) {
        String sqlQuery = "select ID, NAME from RATING WHERE ID IN (SELECT RATING_ID FROM FILM WHERE ID = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    @Override
    public List<Rating> getAllAgeRatings() {
        String sqlQuery = "select ID, NAME from RATING";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
