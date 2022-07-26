package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNameValidationException extends RuntimeException {
    public UserNameValidationException(String msg) {
        super(msg);
    }
}
