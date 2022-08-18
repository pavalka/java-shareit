package ru.practicum.shareit.booking.exceptions;

public class BookingTimeConflictsException extends RuntimeException {
    public BookingTimeConflictsException(String msg) {
        super(msg);
    }
}
