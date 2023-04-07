package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    public Integer id;
    @Email
    public String email;
    @NotNull
    public String login;
    public String name;
    @NotNull
    public LocalDate birthday;
}
