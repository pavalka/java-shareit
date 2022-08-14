package ru.practicum.shareit.item.service;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;

@Getter
@Setter
class BookingInfo {
    private Booking lastBooking;
    private Booking nextBooking;
}
