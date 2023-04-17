package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
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
    public Set<Long> friends;
    public Set<Long> likedMovies;
}
