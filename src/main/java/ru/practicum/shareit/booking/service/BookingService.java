package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingService {
    Booking getBookingById(long userId, long bookingId);

    Collection<Booking> getAllBookingsByUserAndState(long userId, @NonNull BookingState bookingState);

    Collection<Booking> getAllBookingsByOwnerAndState(long ownerId, @NonNull BookingState bookingState);

    Booking createBooking(long userId, long itemId, @NonNull LocalDateTime startTime, @NonNull LocalDateTime endTime);

    Booking setBookingStatus(long ownerId, long bookingId, boolean approved);

    Booking getBookingByIdAndOwner(long userId, long bookingId);
}
