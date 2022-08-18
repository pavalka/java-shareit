package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.BookingInfoDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingInfoMapper {
    public static BookingInfoDto mapBookingToBookingInfoDto(Booking bookingInfo) {
        if (bookingInfo == null) {
            return null;
        }
        return new BookingInfoDto(bookingInfo.getId(), bookingInfo.getUser().getId());
    }
}
