package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

class UserValidationTest {

    User noNameUser = User.builder()
            .id(1L)
            .email("mail@mail.com")
            .login("testUser")
            .name(null)
            .birthday(LocalDate.of(1985, Month.AUGUST, 21))
            .build();

    User finalUser = User.builder()
            .id(1L)
            .email("mail@mail.com")
            .login("testUser")
            .name("testUser")
            .birthday(LocalDate.of(1985, Month.AUGUST, 21))
            .build();

    /**
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    @Test
    void addNoNameUser() {
        ValidateService.validate(noNameUser);
        Assertions.assertEquals(noNameUser, finalUser);
    }
}