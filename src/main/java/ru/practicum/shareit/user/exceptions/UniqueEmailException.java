package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueEmailException extends RuntimeException {
    public UniqueEmailException(String msg) {
        super(msg);
    }
}
