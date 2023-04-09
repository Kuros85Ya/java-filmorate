package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Component
public class ValidateService {
    public static void validate(Film film) {
        if ((film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28)))) throw new ValidationException("Film not valid");
    }

    public static User validate(User user) {
        if (user.name == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
