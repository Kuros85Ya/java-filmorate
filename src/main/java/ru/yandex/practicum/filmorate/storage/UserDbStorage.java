package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmorateMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmorateMapper mapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FilmorateMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public HashMap<Long, User> getUsers() {
        String sqlQuery = "select id, NAME, LOGIN, EMAIL, BIRTHDAY from FILMORATE_USER";
        List<User> users = jdbcTemplate.query(sqlQuery, mapper::mapRowToUser);
        return (HashMap<Long, User>) users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public void update(User user) {
        ValidateService.validate(user);
        String sqlQuery = "update FILMORATE_USER set " +
                "NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? " +
                "where id = ?";
        int rowsChanged = jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());

        if (rowsChanged == 0) {
            throw new NoSuchElementException("Не найден пользователь для обновления");
        }
    }

    @Override
    public User save(User user) {
        User validatedUser = ValidateService.validate(user);

        String sqlQuery = "insert into FILMORATE_USER(LOGIN, NAME, EMAIL, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, validatedUser.getLogin());
            stmt.setString(2, validatedUser.getName());
            stmt.setString(3, validatedUser.getEmail());
            stmt.setDate(4, Date.valueOf(validatedUser.getBirthday()));
            return stmt;
        }, keyHolder);

        validatedUser.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return validatedUser;
    }


    @Override
    public User getUser(Long id) {
        try {
            String sqlQuery = "select id, NAME, LOGIN, EMAIL, BIRTHDAY " +
                    "from FILMORATE_USER where id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, mapper::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Не найден пользователь с таким идентификатором: " + e.getMessage());
        }
    }
}
