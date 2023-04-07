package ru.yandex.practicum.filmorate;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private final String nameBeforeBorder = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
    private final LocalDate borderDate = LocalDate.of(1895, Month.DECEMBER, 28);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    public static class DefaultFilm extends Film {
        public Integer id;
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
     * Название не может быть пустым;
     */
    @Test
    void addEmptyNamedFilm() {
        Film emptyNamed = DefaultFilm.builder().id(1).name("").build();

        assertThrows(NullPointerException.class, () -> ValidateService.validate(emptyNamed));
    }

    /**
     * максимальная длина описания — 200 символов;
     */
    @Test
    void updateFilmShorterDescription() {
        Film longNamedFilm = DefaultFilm.builder().id(1).description(nameBeforeBorder).build();
        ValidateService.validate(longNamedFilm);
    }

    @Test
    void updateFilmLongerDesription() {
        String nameAfterBorder = nameBeforeBorder + "12";
        Film longNamedFilm = DefaultFilm.builder().id(1).description(nameAfterBorder).build();

        assertThrows(ValidationException.class, () -> ValidateService.validate(longNamedFilm));
    }

    @Test
    void updateFilmBorderDescription() {
        String nameBorder = nameBeforeBorder + "1";
        Film longNamedFilm = DefaultFilm.builder().id(1).description(nameBorder).build();
        ValidateService.validate(longNamedFilm);
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

    /**
     * продолжительность фильма должна быть положительной.
     */
    @Test
    void addBadDuration() {
        Film negativeFilm = DefaultFilm.builder().duration(-15).build();
        assertThrows(ValidationException.class, () -> ValidateService.validate(negativeFilm));
    }
}