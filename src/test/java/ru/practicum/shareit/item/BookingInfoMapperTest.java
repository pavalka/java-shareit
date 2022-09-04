package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class BookingInfoMapperTest {
    private long nextUserId = 1;
    private long nextBookingId = 1;
    private LocalDateTime nextDateTime = LocalDateTime.of(2022, 8, 25, 10, 23, 0);

    @Test
    void mapBookingToBookingInfoDtoReturnNullWhenArgumentIsNull() {
        assertNull(BookingInfoMapper.mapBookingToBookingInfoDto(null));
    }

    @Test
    void mapBookingToBookingInfoDtoReturnDto() {
        var user = createUser();
        var booking = createBooking(user, getNextDateTime());

        var bookingInfoDto = BookingInfoMapper.mapBookingToBookingInfoDto(booking);

        assertEquals(booking.getId(), bookingInfoDto.getId());
        assertEquals(booking.getUser().getId(), bookingInfoDto.getBookerId());
    }

    private User createUser() {
        var user = new User();
        var userId = getNextUserId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Booking createBooking(User author, LocalDateTime start) {
        var booking = new Booking();
        var bookingId = getNextBookingId();

        booking.setId(bookingId);
        booking.setUser(author);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(start);
        booking.setEndTime(start.plusDays(1));
        return booking;
    }

    private long getNextUserId() {
        return nextUserId++;
    }

    private long getNextBookingId() {
        return nextBookingId++;
    }

    private LocalDateTime getNextDateTime() {
        nextDateTime = nextDateTime.plusDays(1);
        return nextDateTime;
    }
}