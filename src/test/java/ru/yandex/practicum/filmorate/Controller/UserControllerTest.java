package ru.yandex.practicum.filmorate.Controller;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController = new UserController();

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    public static class DefaultUser extends User {

        public Integer id;
        @Email
        @Builder.Default
        public String email = "mail@mail.com";
        @Builder.Default
        public String login = "testUser";
        @Builder.Default
        public String name = "Тест Тестович";
        @Builder.Default
        public LocalDate birthday = LocalDate.of(1985, Month.AUGUST, 21);
    }

    /**
     * электронная почта не может быть пустой и должна содержать символ @;
     */
    @Test
    void addWrongEmailUser() {
        User wrongEmail = DefaultUser.builder().id(1).email("mail.mail").build();

        assertThrows(ValidationException.class, () -> userController.addUser(wrongEmail));
    }

    @Test
    void addEmptyEmailUser() {
        User wrongEmail = DefaultUser.builder().id(1).email("").build();

        assertThrows(ValidationException.class, () -> userController.addUser(wrongEmail));
    }

    /**
     * логин не может быть пустым и содержать пробелы;
     */
    @Test
    void addEmptyLoginUser() {
        User wrongLogin = DefaultUser.builder().id(1).login("").build();

        assertThrows(ValidationException.class, () -> userController.addUser(wrongLogin));
    }

    @Test
    void addWrongLoginUser() {
        User wrongLogin = DefaultUser.builder().id(1).login("asdf asdf").build();

        assertThrows(ValidationException.class, () -> userController.addUser(wrongLogin));
    }

    /**
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    @Test
    void addNoNameUser() {
        User noNameUser = DefaultUser.builder().id(1).name(null).build();
        User user = userController.addUser(noNameUser);
        Assertions.assertEquals(DefaultUser.builder().id(1).name(noNameUser.getLogin()).build(), user);
    }

    /**
     * Дата рождения не может быть в будущем.
     */
    @Test
    void addWrongBirthdateUser() {
        User wrongBirthday = DefaultUser.builder().id(1).birthday(LocalDate.now().plusDays(1)).build();

        assertThrows(ValidationException.class, () -> userController.addUser(wrongBirthday));
    }
}