package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

        String sqlQuery = "insert into FILM(NAME, DESCRIPTION, DURATION, RELEASE_DATE) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "update FILM set " +
                "NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , film.getReleaseDate());
    }

    @Override
    public HashMap<Long, Film> getFilms() {
        String sqlQuery = "select ID, NAME, DESCRIPTION, DURATION, RELEASE_DATE from FILM";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return (HashMap<Long, Film>) films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    public HashMap<Long, Film> getGenres() {
        String sqlQuery = "select ID, NAME, DESCRIPTION, DURATION, RELEASE_DATE from FILM";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return (HashMap<Long, Film>) films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    @Override
    public Film getFilm(Long id) {
        String sqlQuery = "select id, NAME, DESCRIPTION, DURATION, RELEASE_DATE " +
                "from FILM where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
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
}
