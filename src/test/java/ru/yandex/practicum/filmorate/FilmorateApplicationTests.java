package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User noNameUser = User.builder()
                .email("mail@mail.com")
                .login("testUser")
                .name(null)
                .birthday( LocalDate.of(1985, Month.AUGUST, 21))
                .build();

        userStorage.save(noNameUser);

        User user = userStorage.getUser(1L);
        assertThat(Optional.ofNullable(user))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

}
