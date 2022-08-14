package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingToCreateCommentNotFoundException extends RuntimeException {
    public BookingToCreateCommentNotFoundException(String msg) {
        super(msg);
    }
}
