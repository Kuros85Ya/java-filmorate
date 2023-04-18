package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = {"classpath:add_test_objects.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:remove_test_objects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class FilmDbServiceTest {
    private final FilmDbService filmService;
    private final FilmDbStorage filmStorage;
    private final UserDbService userService;
    private final UserDbStorage userStorage;

    Rating defaultRating = Rating.builder()
            .id(1L)
            .name("G")
            .build();

    User defaultUser = User.builder()
            .id(1L)
            .email("mail@mail.com")
            .login("testUser")
            .name(null)
            .birthday(LocalDate.of(1985, Month.AUGUST, 21))
            .build();
    Film defaultFilm = Film.builder()
            .id(1L)
            .name("Default Name")
            .description("Default description")
            .duration(120)
            .releaseDate(LocalDate.of(2005, Month.APRIL, 12))
            .mpa(defaultRating)
            .genres(new ArrayList<>())
            .userLiked(new HashSet<>())
            .build();

    @Test
    void save() {
        Film nonSavableFilm = Film.builder()
                .id(2L)
                .build();

        assertThrows(ValidationException.class, () -> filmStorage.save(nonSavableFilm));
    }

    @Test
    void getFilm() {
        Film film = filmStorage.getFilm(1L);
        assertThat(Optional.ofNullable(film))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Default Name")
                                .hasFieldOrPropertyWithValue("description", "Default description")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2005, Month.APRIL, 12))
                                .hasFieldOrPropertyWithValue("mpa", defaultRating)
                );
    }

    @Test
    void update() {
        Rating updatedRating = Rating.builder()
                .id(2L)
                .name("PG")
                .build();

        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated description")
                .duration(122)
                .releaseDate(LocalDate.of(2006, Month.APRIL, 12))
                .mpa(updatedRating)
                .build();

        filmStorage.update(updatedFilm);
        Film testFilm = filmStorage.getFilm(updatedFilm.getId());

        assertThat(Optional.ofNullable(testFilm))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Updated Name")
                                .hasFieldOrPropertyWithValue("description", "Updated description")
                                .hasFieldOrPropertyWithValue("duration", 122)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2006, Month.APRIL, 12))
                                .hasFieldOrPropertyWithValue("mpa", updatedRating)
                );
    }

    @Test
    void getFilms() {
        HashMap<Long, Film> films = filmStorage.getFilms();
        Assertions.assertEquals(1, films.size());
        assertThat(Optional.ofNullable(films.get(1L)))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Default Name")
                                .hasFieldOrPropertyWithValue("description", "Default description")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2005, Month.APRIL, 12))
                                .hasFieldOrPropertyWithValue("mpa", defaultRating)
                );
    }

    @Test
    void addLike() {
        filmService.addLike(defaultUser, defaultFilm);
    }

    @Test
    void addLikeToNonExistingFilm() {
        Film nonSavedFilm = Film.builder()
                .id(3L)
                .build();

        assertThrows(NoSuchElementException.class, () -> filmService.addLike(defaultUser, nonSavedFilm));
    }

    @Test
    void addLikeToNonExistingUser() {
        User nonSavedUser = User.builder()
                .id(2L)
                .build();

        assertThrows(NoSuchElementException.class, () -> filmService.addLike(nonSavedUser, defaultFilm));
    }

    @Test
    void removeNonExistentLike() {
        assertThrows(NoSuchElementException.class, () -> filmService.removeLike(defaultUser, defaultFilm));
    }

    @Test
    void removeLike() {
        filmService.addLike(defaultUser, defaultFilm);
        filmService.removeLike(defaultUser, defaultFilm);
    }

    @Test
    void getMostPopularMovies() {
        List<Film> films = filmService.getMostPopularMovies(1);
        Assertions.assertEquals(1, films.size());
        assertThat(Optional.ofNullable(films.get(0)))
                .isPresent()
                .hasValueSatisfying(it ->
                        assertThat(it).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Default Name")
                                .hasFieldOrPropertyWithValue("description", "Default description")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2005, Month.APRIL, 12))
                                .hasFieldOrPropertyWithValue("mpa", defaultRating)
                );
    }

    @Test
    void getFilmGenres() {
        filmService.getFilmGenres(1L);
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = filmService.getAllGenres();
    }

    @Test
    void getGenre() {
    }

    @Test
    void getAllAgeRatings() {
    }

    @Test
    void getAgeRating() {
    }
}