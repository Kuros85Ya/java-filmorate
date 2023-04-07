package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    public Long id;
    @Email
    public String email;
    @NotNull
    public String login;
    public String name;
    @NotNull
    public LocalDate birthday;
}
