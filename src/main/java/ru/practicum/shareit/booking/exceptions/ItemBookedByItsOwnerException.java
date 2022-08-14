package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemBookedByItsOwnerException extends RuntimeException {
    public ItemBookedByItsOwnerException(String msg) {
        super(msg);
    }
}
