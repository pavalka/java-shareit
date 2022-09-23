package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto getBookingById(long userId, long bookingId);

    Collection<BookingDto> getAllBookingsByUserAndState(long userId, @NonNull BookingState bookingState,
                                                        long from, int size);

    Collection<BookingDto> getAllBookingsByOwnerAndState(long ownerId, @NonNull BookingState bookingState,
                                                         long from, int size);

    BookingDto createBooking(long userId, @NonNull BookingRequestDto bookingDto);

    BookingDto setBookingStatus(long ownerId, long bookingId, boolean approved);
}
