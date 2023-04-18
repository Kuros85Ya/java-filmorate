package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = {"classpath:add_test_objects.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:remove_test_objects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserDbServiceTest {

    private final UserDbService userService;
    private final UserDbStorage userStorage;

    @Test
    public void save() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser2")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();


        User user = userStorage.save(noNameUser);

        assertThat(Optional.ofNullable(user))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it)
                                .hasFieldOrPropertyWithValue("email", "mail@mail.com")
                                .hasFieldOrPropertyWithValue("login", "testUser2")
                                .hasFieldOrPropertyWithValue("name", "testUser2")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1985, Month.AUGUST, 21))
                );
    }

    @Test
    public void getUser() {
        User user = userStorage.getUser(0L);
        assertThat(Optional.ofNullable(user))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 0L)
                                .hasFieldOrPropertyWithValue("email", "mail@mail.com")
                                .hasFieldOrPropertyWithValue("login", "testUser")
                                .hasFieldOrPropertyWithValue("name", "name")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1985, Month.AUGUST, 21)
                                ));
    }

    @Test
    public void getUsers() {
        HashMap<Long, User> users = userStorage.getUsers();
        Assertions.assertEquals(users.size(), 1);
        Assertions.assertEquals(users.get(0L), User.builder()
                .id(0L)
                .email("mail@mail.com")
                .login("testUser")
                .name("name")
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build());
    }

    @Test
    void update() {
        User updatedUser = User.builder()
                .id(0L)
                .email("updated@mail.com")
                .login("updatedLogin")
                .name(null)
                .birthday(LocalDate.of(1986, Month.AUGUST, 21))
                .build();

        userStorage.update(updatedUser);

        User testUser = userStorage.getUser(0L);

        assertThat(Optional.ofNullable(testUser))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 0L)
                                .hasFieldOrPropertyWithValue("email", "updated@mail.com")
                                .hasFieldOrPropertyWithValue("login", "updatedLogin")
                                .hasFieldOrPropertyWithValue("name", "updatedLogin")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1986, Month.AUGUST, 21))
                );
    }

    @Test
    void addFriend() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser2")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();
        userStorage.save(noNameUser);
        userService.addFriend(noNameUser, userStorage.getUser(0L));
        List<User> friends = userService.getFriends(userStorage.getUser(0L));
        Assertions.assertEquals(friends.size(), 0);
    }

    @Test
    void getFriends() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser2")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();
        userStorage.save(noNameUser);

        userService.addFriend(noNameUser, userStorage.getUser(0L));
        userService.addFriend(userStorage.getUser(0L), noNameUser);
        List<User> friends = userService.getFriends(userStorage.getUser(0L));
        Assertions.assertEquals(friends.size(), 1);
        Assertions.assertEquals(friends.get(0), noNameUser);
    }

    @Test
    void removeFriend() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser2")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();
        userStorage.save(noNameUser);

        userService.addFriend(noNameUser, userStorage.getUser(0L));
        userService.addFriend(userStorage.getUser(0L), noNameUser);
        List<User> friends = userService.getFriends(userStorage.getUser(0L));
        Assertions.assertEquals(friends.size(), 1);

        userService.removeFriend(userStorage.getUser(0L), noNameUser);
        List<User> friends2 = userService.getFriends(userStorage.getUser(0L));
        Assertions.assertEquals(friends2.size(), 0);
    }

    @Test
    void getCommonFriends() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser2")
                .name(null)
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();
        userStorage.save(noNameUser);

        User testUser3 = User.builder()
                .email("mail@mail.com")
                .login("testUser3")
                .name("Common")
                .birthday(LocalDate.of(1985, Month.AUGUST, 21))
                .build();
        userStorage.save(testUser3);

        userService.addFriend(testUser3, userStorage.getUser(0L));
        userService.addFriend(userStorage.getUser(0L), testUser3);

        userService.addFriend(testUser3, userStorage.getUser(2L));
        userService.addFriend(userStorage.getUser(2L), testUser3);

        List<User> friends = userService.getCommonFriends(userStorage.getUser(0L), userStorage.getUser(2L));
        Assertions.assertEquals(friends.size(), 1);
        Assertions.assertEquals(friends.get(0), testUser3);

    }
}