package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbServiceTest {

    private final FilmDbService filmService;
    private final FilmDbStorage filmStorage;
    private final UserDbService userService;
    private final UserDbStorage userStorage;

    @Test
    public void testSaveUser() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();

        User user = userStorage.save(noNameUser);

        assertThat(Optional.ofNullable(user))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testFindUserById() {

        User user = userStorage.getUser(1L);
        assertThat(Optional.ofNullable(user))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("login", 1L)
                );
    }

    @Test
    public void testGetAllUsers() {
        HashMap<Long, User> users = userStorage.getUsers();
        Assertions.assertEquals(users.size(), 1);
        Assertions.assertEquals(users.get(1L), User.builder()
                .email("mail@mail.com")
                .login("testUser")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build());
    }

    @Test
    void getUsers() {
    }

    @Test
    void update() {
    }

    @Test
    void save() {
    }

    @Test
    void getUser() {
    }

    @Test
    void addFriend() {
    }

    @Test
    void removeFriend() {
    }

    @Test
    void getCommonFriends() {
    }

    @Test
    void getFriends() {
    }
}