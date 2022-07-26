package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserEmailValidationException extends RuntimeException {
    public UserEmailValidationException(String msg) {
        super(msg);
    }
}
