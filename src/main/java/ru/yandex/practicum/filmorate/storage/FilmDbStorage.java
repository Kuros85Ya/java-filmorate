package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        ValidateService.validate(film);

        String sqlQuery = "insert into FILM(NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(5, film.mpa.id);
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            film.getGenres().forEach(it -> setFilmGenres(film.getId(), it.getId()));
        }
        return film;
    }

    private void deleteFilmGenres(Long filmId) {
        String sqlQuery = "delete from FILM_GENRE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void setFilmGenres(Long filmId, Long genreId) {
        try {
            String sqlQuery = "insert into FILM_GENRE (FILM_ID, GENRE_ID) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genreId);
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILM set " +
                "NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?, RATING_ID = ?" +
                "where id = ?";
        int rowsChanged = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , film.getReleaseDate()
                , film.getMpa().getId()
                , film.getId());
        if (rowsChanged == 0) {
            throw new NoSuchElementException("Не фильм для обновления");
        }
        if (film.getGenres() == null) {
            deleteFilmGenres(film.getId());
        }

        if (film.getGenres() != null) {
            deleteFilmGenres(film.getId());
            film.getGenres().forEach(it -> setFilmGenres(film.getId(), it.getId()));
            film.setGenres(getFilmGenres(film.getId()));
        }
        return film;
    }

    @Override
    public HashMap<Long, Film> getFilms() {
        String sqlQuery = "select FILM.ID, FILM.NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID, R.NAME as RATING_NAME from FILM JOIN RATING R on R.ID = FILM.RATING_ID";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        films.forEach(it -> it.setGenres(getFilmGenres(it.getId())));
        return (HashMap<Long, Film>)
                films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    private List<Genre> getFilmGenres(Long id) {
        String sqlQuery = "select ID, NAME from GENRE WHERE ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public Film getFilm(Long id) {
        try {
            String sqlQuery = "select FILM.ID, FILM.NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID, R.NAME as RATING_NAME from FILM JOIN RATING R on R.ID = FILM.RATING_ID" +
                    " where FILM.id = ?";
            Film finalFilm = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            if (finalFilm != null) {
                finalFilm.setGenres(getFilmGenres(finalFilm.getId()));
            }
            return finalFilm;
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
}
