package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface BookingService {
    Booking getBookingById(@NonNull User user, long bookingId);

    Collection<Booking> getAllBookingsByUserAndState(@NonNull User user, @NonNull BookingState bookingState);

    Collection<Booking> getAllBookingsByOwnerAndState(@NonNull User owner, @NonNull BookingState bookingState);

    Booking createBooking(@NonNull Booking booking);

    Booking setBookingStatus(@NonNull User owner, long bookingId, boolean approved);
}
