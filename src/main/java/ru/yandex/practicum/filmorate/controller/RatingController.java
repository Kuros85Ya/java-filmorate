package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/mpa")
public class RatingController {

    private final FilmService service;

    @Autowired
    public RatingController(@Qualifier("filmDbService") FilmService service) {
        this.service = service;
    }

    /**
     * Получение списка возрастных рейтингов.
     */
    @GetMapping
    public List<Rating> getAllRatings() {
        log.info("Запрошены все рейтинги");
        return service.getAllAgeRatings();
    }

    /**
     * Получение списка жанров.
     */
    @GetMapping("/{id}")
    public Rating getSingleRating(@PathVariable Long id) {
        log.info("Запрошен возрастной рейтинг + " + id);
        return service.getFilmAgeRating(id);
    }
}
