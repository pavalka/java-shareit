package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemAvailabilityValidationException extends RuntimeException {
    public ItemAvailabilityValidationException(String msg) {
        super(msg);
    }
}
