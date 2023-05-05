package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.FilmorateMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserDbService implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final FilmorateMapper mapper;

    public UserDbService(JdbcTemplate jdbcTemplate, FilmorateMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public void addFriend(User initiator, User acceptor) {
        String sqlQuery = "insert into FRIEND (INITIATOR_ID, ACCEPTOR_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());
    }

    @Override
    public void removeFriend(User initiator, User acceptor) {
        String sqlQuery = "delete from FRIEND where INITIATOR_ID = ? AND ACCEPTOR_ID = ?";
        int rowsChanged = jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());

        if (rowsChanged == 0) {
            throw new NoSuchElementException("Не найден друг для удаления");
        }
    }

    @Override
    public List<User> getCommonFriends(User initiator, User acceptor) {
        String sqlQuery = "SELECT ID, NAME, LOGIN, EMAIL, BIRTHDAY FROM FILMORATE_USER WHERE ID IN" +
                "(SELECT ACCEPTOR_ID from FRIEND WHERE INITIATOR_ID = ? AND ACCEPTOR_ID IN (SELECT ACCEPTOR_ID FROM FRIEND WHERE INITIATOR_ID = ?))";
        return jdbcTemplate.query(sqlQuery, mapper::mapRowToUser, initiator.getId(), acceptor.getId());
    }

    @Override
    public List<User> getFriends(User initiator) {
        String sqlQuery = "SELECT ID, NAME, LOGIN, EMAIL, BIRTHDAY FROM FILMORATE_USER WHERE ID IN" +
                "(SELECT ACCEPTOR_ID from FRIEND WHERE INITIATOR_ID = ?)";
        return jdbcTemplate.query(sqlQuery, mapper::mapRowToUser, initiator.getId());
    }
}
