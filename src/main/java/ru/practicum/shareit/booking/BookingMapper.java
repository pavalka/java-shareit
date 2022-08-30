package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingOutgoingDto mapBookingToDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        var bookingDto = new BookingOutgoingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setItem(ItemMapper.mapItemToItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.mapUserToUserDto(booking.getUser()));
        bookingDto.setStart(booking.getStartTime());
        bookingDto.setEnd(booking.getEndTime());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static Collection<BookingOutgoingDto> mapBookingCollectionToDto(Collection<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream().map(BookingMapper::mapBookingToDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public static Booking mapDtoToBooking(BookingIncomingDto bookingDto, Item item, User user) {
        if (bookingDto == null) {
            return null;
        }

        var booking = new Booking();

        booking.setUser(user);
        booking.setItem(item);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        return booking;
    }
}
