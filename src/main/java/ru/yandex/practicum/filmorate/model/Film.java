package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    public Integer id;
    @NotNull
    public String name;
    @NotNull
    public String description;
    @NotNull
    public Integer duration;
    @NotNull
    public LocalDate releaseDate;
}
