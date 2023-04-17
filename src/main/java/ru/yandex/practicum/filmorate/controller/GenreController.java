package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/genres")
public class GenreController {

    private final FilmService service;

    @Autowired
    public GenreController(@Qualifier("filmDbService") FilmService service) {
        this.service = service;
    }

    /**
     * Получение списка жанров.
     */
    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Запрошены все жанры");
        return service.getAllGenres();
    }

    /**
     * Получение конкретного жанра.
     */
    @GetMapping("/{id}")
    public List<Genre> getSingleGenre(@PathVariable Long id) {
        log.info("Запрошены жанры по фильму + " + id);
        return service.getFilmGenres(id);
    }
}
