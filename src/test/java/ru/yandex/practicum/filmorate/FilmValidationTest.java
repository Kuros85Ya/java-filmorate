package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private final LocalDate borderDate = LocalDate.of(1895, Month.DECEMBER, 28);

    Film defaultFilm = Film.builder()
            .name("Default Name")
            .description("Default description")
            .duration(120)
            .releaseDate(LocalDate.of(2005, Month.APRIL, 12))
            .userLiked(new HashSet<>())
            .mpa(null)
            .genres(null)
            .build();

    /**
     * дата релиза — не раньше 28 декабря 1895 года;
     */
    @Test
    void addOldFilm() {
        defaultFilm.setReleaseDate(borderDate.minusDays(1));
        assertThrows(ValidationException.class, () -> ValidateService.validate(defaultFilm));
    }

    @Test
    void addOldBeforeBorder() {
        defaultFilm.setReleaseDate(borderDate.plusDays(1));
        ValidateService.validate(defaultFilm);
    }

    @Test
    void addOldBorder() {
        defaultFilm.setReleaseDate(borderDate);
        ValidateService.validate(defaultFilm);
    }
}