package ru.practicum.shareit.item.exceptions;

public class UserIsNotItemOwnerException extends RuntimeException {
    public UserIsNotItemOwnerException(String msg) {
        super(msg);
    }
}
