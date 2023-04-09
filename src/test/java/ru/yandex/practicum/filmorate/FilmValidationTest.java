package ru.yandex.practicum.filmorate;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private final LocalDate borderDate = LocalDate.of(1895, Month.DECEMBER, 28);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    public static class DefaultFilm extends Film {
        public Long id;
        @Builder.Default
        public String name = "Default Name";
        @Builder.Default
        public String description = "Default description";
        @Builder.Default
        public Integer duration = 120;
        @Builder.Default
        public LocalDate releaseDate = LocalDate.of(2005, Month.APRIL, 12);
    }

    /**
     * дата релиза — не раньше 28 декабря 1895 года;
     */
    @Test
    void addOldFilm() {
        Film oldFilm = DefaultFilm.builder().releaseDate(borderDate.minusDays(1)).build();

        assertThrows(ValidationException.class, () -> ValidateService.validate(oldFilm));
    }

    @Test
    void addOldBeforeBorder() {
        Film oldFilm = DefaultFilm.builder().releaseDate(borderDate.plusDays(1)).build();
        ValidateService.validate(oldFilm);
    }

    @Test
    void addOldBorder() {
        Film oldFilm = DefaultFilm.builder().releaseDate(borderDate).build();
        ValidateService.validate(oldFilm);
    }
}