package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemDescriptionValidationException extends RuntimeException {
    public ItemDescriptionValidationException(String msg) {
        super(msg);
    }
}
