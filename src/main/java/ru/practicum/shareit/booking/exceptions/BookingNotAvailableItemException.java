package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingNotAvailableItemException extends RuntimeException {
    public BookingNotAvailableItemException(String msg) {
        super(msg);
    }
}
