package ru.yandex.practicum.filmorate;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.Month;

class UserValidationTest {

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    public static class DefaultUser extends User {

        public Long id;
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
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    @Test
    void addNoNameUser() {
        User noNameUser = DefaultUser.builder().id(1L).name(null).build();
        User user = ValidateService.validate(noNameUser);
        Assertions.assertEquals(DefaultUser.builder().id(1L).name(noNameUser.getLogin()).build(), user);
    }
}