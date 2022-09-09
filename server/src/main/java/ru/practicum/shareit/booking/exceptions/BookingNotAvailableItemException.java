package ru.practicum.shareit.booking.exceptions;

public class BookingNotAvailableItemException extends RuntimeException {
    public BookingNotAvailableItemException(String msg) {
        super(msg);
    }
}
