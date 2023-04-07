package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    public Long id;
    @NotNull
    public String name;
    @NotNull
    public String description;
    @NotNull
    public Integer duration;
    @NotNull
    public LocalDate releaseDate;
}
