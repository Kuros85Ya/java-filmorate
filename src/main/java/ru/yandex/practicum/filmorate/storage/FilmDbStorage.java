package ru.yandex.practicum.filmorate.storage;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmorateMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmorateMapper mapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, FilmorateMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.mapper = mapper;
    }

    @SneakyThrows
    @Override
    public Film save(Film film) {
        try {
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
                setFilmGenres(film.getId(), film.getGenres());
            }
            return film;
        } catch (Exception e) {
            throw new ValidationException("Объект фильма не подходит для сохранения в базу " + e.getMessage());
        }
    }

    private void deleteFilmGenres(Long filmId) {
        String sqlQuery = "delete from FILM_GENRE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void setFilmGenres(Long filmId, List<Genre> genres) {
        String sql = "insert into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?)";
        try {
            jdbcTemplate.batchUpdate(sql, genres, 2,
                    (ps, p) -> {
                        ps.setLong(1, filmId);
                        ps.setLong(2, p.getId());
                    });
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILM set " +
                "NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?, RATING_ID = ?" +
                "where id = ?";
        int rowsChanged = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(), film.getMpa().getId(), film.getId());
        if (rowsChanged == 0) {
            throw new NoSuchElementException("Не найден фильм для обновления");
        }
        if (film.getGenres() == null) {
            deleteFilmGenres(film.getId());
        }

        if (film.getGenres() != null) {
            deleteFilmGenres(film.getId());
            setFilmGenres(film.getId(), film.getGenres());
            film.setGenres(getFilmGenres(film.getId()));
        }
        return film;
    }

    @Override
    public HashMap<Long, Film> getFilms() {
        String sqlQueryFilms = "select FILM.ID, FILM.NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID, R.NAME as RATING_NAME from FILM JOIN RATING R on R.ID = FILM.RATING_ID";
        List<Film> films = jdbcTemplate.query(sqlQueryFilms, mapper::mapRowToFilm);

        setFilmGenres(films);
        return (HashMap<Long, Film>) films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    @Override
    public void setFilmGenres(List<Film> films) {
        String sqlQueryGenres = "select FILM_ID, g.ID as genre_id, g.NAME as genre_name from FILM_GENRE fg join GENRE G on G.ID = fg.GENRE_ID WHERE FILM_ID IN (:ids)";
        Set<Long> ids = films.stream().map(Film::getId).collect(Collectors.toSet());
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        namedParameterJdbcTemplate.query(sqlQueryGenres, parameters, (rs, rn) -> mapper.mapRowToFilmsWithGenres(rs, rn, films));
    }

    private List<Genre> getFilmGenres(Long id) {
        String sqlQuery = "select ID, NAME from GENRE WHERE ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, mapper::mapRowToGenre, id);
    }

    @Override
    public Film getFilm(Long id) {
        try {
            String sqlQuery = "select FILM.ID, FILM.NAME, DESCRIPTION, DURATION, RELEASE_DATE, RATING_ID, R.NAME as RATING_NAME from FILM JOIN RATING R on R.ID = FILM.RATING_ID" +
                    " where FILM.id = ?";
            Film finalFilm = jdbcTemplate.queryForObject(sqlQuery, mapper::mapRowToFilm, id);
            if (finalFilm != null) {
                finalFilm.setGenres(getFilmGenres(finalFilm.getId()));
            }
            return finalFilm;
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Не найден фильм с таким идентификатором: " + e.getMessage());
        }
    }
}
