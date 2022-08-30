package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;

import java.util.Collection;

public interface BookingService {
    BookingOutgoingDto getBookingById(long userId, long bookingId);

    Collection<BookingOutgoingDto> getAllBookingsByUserAndState(long userId, @NonNull BookingState bookingState,
                                                                long from, int size);

    Collection<BookingOutgoingDto> getAllBookingsByOwnerAndState(long ownerId, @NonNull BookingState bookingState,
                                                                 long from, int size);

    BookingOutgoingDto createBooking(long userId, @NonNull BookingIncomingDto bookingDto);

    BookingOutgoingDto setBookingStatus(long ownerId, long bookingId, boolean approved);
}
