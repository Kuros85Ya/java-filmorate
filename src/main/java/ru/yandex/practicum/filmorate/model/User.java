package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    public Long id;
    @Email
    public String email;
    @NotEmpty
    @Pattern(regexp = "\\S*")
    public String login;
    public String name;
    @NotNull
    @Past
    public LocalDate birthday;
    public Set<Long> friends = new HashSet<>();
    public Set<Long> likedMovies = new HashSet<>();
}
