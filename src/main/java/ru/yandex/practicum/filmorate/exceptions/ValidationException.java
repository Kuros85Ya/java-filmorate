package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Error {
    public ValidationException(final String message) {
        super(message);
    }
}