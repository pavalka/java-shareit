package ru.practicum.shareit.item.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String msg) {
        super(msg);
    }
}
