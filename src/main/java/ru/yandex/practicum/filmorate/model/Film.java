package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    public Long id;
    @NotEmpty
    public String name;
    @NotNull
    @Size(max = 200)
    public String description;
    @NotNull
    @PositiveOrZero
    public Integer duration;
    @NotNull
    public LocalDate releaseDate;
    public Set<Long> userLiked = new HashSet<>();
}
