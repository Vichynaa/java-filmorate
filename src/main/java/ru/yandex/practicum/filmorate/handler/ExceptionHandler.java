package ru.yandex.practicum.filmorate.handler;

import exception.NotFoundException;
import exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse handleValidationOverflow(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse handleNotFoundOverflow(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse handleInternalServerErrorOverflow(final RuntimeException e) {
        return new ErrorResponse("Ошибка сервера");
    }
}
