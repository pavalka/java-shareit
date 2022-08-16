package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;

import java.util.Collection;

public interface MainBookingService {
    BookingOutgoingDto getBookingById(long userId, long bookingId);

    Collection<BookingOutgoingDto> getAllBookingsByUserAndState(long userId, BookingState bookingState);

    Collection<BookingOutgoingDto> getAllBookingsByOwnerAndState(long ownerId, BookingState bookingState);

    BookingOutgoingDto createBooking(long userId, BookingIncomingDto bookingDto);

    BookingOutgoingDto setBookingStatus(long ownerId, long bookingId, boolean approved);
}
