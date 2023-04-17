package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserDbService implements UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        jdbcTemplate.update(sqlQuery,
                initiator.getId(),
                acceptor.getId());
    }

    @Override
    public List<User> getCommonFriends(User initiator, User acceptor) {
        String sqlQuery = "SELECT ID, NAME, LOGIN, EMAIL, BIRTHDAY FROM FILMORATE_USER WHERE ID IN" +
                "(SELECT ACCEPTOR_ID from FRIEND WHERE INITIATOR_ID = ? AND INITIATOR_ID IN (SELECT ACCEPTOR_ID FROM FRIEND WHERE INITIATOR_ID = ?))";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
