package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalBookingApproveException extends RuntimeException {
    public IllegalBookingApproveException(String msg) {
        super(msg);
    }
}
