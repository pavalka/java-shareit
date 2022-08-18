package ru.practicum.shareit.booking.exceptions;

public class ItemBookedByItsOwnerException extends RuntimeException {
    public ItemBookedByItsOwnerException(String msg) {
        super(msg);
    }
}
