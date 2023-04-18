package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

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
                acceptor.getId(),
                initiator.getId());
    }

    @Override
    public void removeLike(User initiator, Film acceptor) {
        String sqlQuery = "delete from USER_FILM_LIKES where USER_ID = ? AND FILM_ID = ?";
        int rowsChanged = jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());
        if (rowsChanged == 0) {
            throw new NoSuchElementException("Не найден лайк для удаления");
        }
    }

    @Override
    public List<Film> getMostPopularMovies(int numberOfMovies) {

        String sqlQuery = "select FILM.ID, FILM.NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID, R.NAME as RATING_NAME, likes.LIKES " +
                "                from FILM left join\n" +
                "(SELECT FILM_ID, COUNT(*) as LIKES FROM USER_FILM_LIKES GROUP BY FILM_ID) likes " +
                " on likes.FILM_ID = FILM.ID " +
                " JOIN RATING R on R.ID = FILM.RATING_ID " +
                " order by LIKES desc LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, numberOfMovies);
        films.forEach(it -> it.setGenres(getFilmGenres(it.getId())));
        return films;
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
    public Genre getGenre(Long id) {
        try {
            String sqlQuery = "select ID, NAME from GENRE WHERE ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Не найден фильм с таким идентификатором: " + e.getMessage());
        }
    }

    @Override
    public List<Rating> getAllAgeRatings() {
        String sqlQuery = "select ID, NAME from RATING";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating getAgeRating(Long id) {
        try {
            String sqlQuery = "select ID, NAME from RATING WHERE ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Не найден фильм с таким идентификатором: " + e.getMessage());
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(Rating.builder().id(resultSet.getLong("rating_id")).name(resultSet.getString("rating_name")).build())
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
