package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    private long nextId = 1;

    @Test
    void mapBookingToDtoReturnNullIfArgumentIsNull() {
        assertNull(BookingMapper.mapBookingToDto(null));
    }

    @Test
    void mapBookingToDtoReturnBookingDto() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var created = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, created);

        var bookingDto = assertDoesNotThrow(() -> BookingMapper.mapBookingToDto(booking));

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(item.getId(), bookingDto.getItem().getId());
        assertEquals(item.getName(), bookingDto.getItem().getName());
        assertEquals(item.getDescription(), bookingDto.getItem().getDescription());
        assertEquals(item.getAvailable(), bookingDto.getItem().getAvailable());
        assertNull(bookingDto.getItem().getLastBooking());
        assertNull(bookingDto.getItem().getNextBooking());
        assertNull(bookingDto.getItem().getComments());
        assertNull(bookingDto.getItem().getRequestId());
        assertEquals(bookingAuthor.getId(), bookingDto.getBooker().getId());
        assertEquals(bookingAuthor.getName(), bookingDto.getBooker().getName());
        assertEquals(bookingAuthor.getEmail(), bookingDto.getBooker().getEmail());
        assertEquals(booking.getStartTime(), bookingDto.getStart());
        assertEquals(booking.getEndTime(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void mapBookingCollectionToDtoReturnNullWhenArgumentIsNull() {
        assertNull(BookingMapper.mapBookingCollectionToDto(null));
    }

    @Test
    void mapBookingCollectionToDtoReturnDtoCollection() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var created = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, created);

        var bookingDto = assertDoesNotThrow(
                () -> BookingMapper.mapBookingCollectionToDto(List.of(booking)).toArray(new BookingDto[1]));

        assertEquals(1, bookingDto.length);
        assertEquals(booking.getId(), bookingDto[0].getId());
        assertEquals(item.getId(), bookingDto[0].getItem().getId());
        assertEquals(item.getName(), bookingDto[0].getItem().getName());
        assertEquals(item.getDescription(), bookingDto[0].getItem().getDescription());
        assertEquals(item.getAvailable(), bookingDto[0].getItem().getAvailable());
        assertNull(bookingDto[0].getItem().getLastBooking());
        assertNull(bookingDto[0].getItem().getNextBooking());
        assertNull(bookingDto[0].getItem().getComments());
        assertNull(bookingDto[0].getItem().getRequestId());
        assertEquals(bookingAuthor.getId(), bookingDto[0].getBooker().getId());
        assertEquals(bookingAuthor.getName(), bookingDto[0].getBooker().getName());
        assertEquals(bookingAuthor.getEmail(), bookingDto[0].getBooker().getEmail());
        assertEquals(booking.getStartTime(), bookingDto[0].getStart());
        assertEquals(booking.getEndTime(), bookingDto[0].getEnd());
        assertEquals(booking.getStatus(), bookingDto[0].getStatus());
    }

    @Test
    void mapDtoToBookingReturnNullIfArgumentIsNull() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        assertNull(BookingMapper.mapDtoToBooking(null, item, bookingAuthor));
    }

    @Test
    void mapDtoToBookingReturnBooking() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var bookingDto = createIncomingBookingDto(item.getId(), LocalDateTime.now());

        var booking = assertDoesNotThrow(() -> BookingMapper.mapDtoToBooking(bookingDto, item, bookingAuthor));

        assertEquals(bookingDto.getItemId(), booking.getItem().getId());
        assertEquals(bookingDto.getStart(), booking.getStartTime());
        assertEquals(bookingDto.getEnd(), booking.getEndTime());
        assertEquals(item.getName(), booking.getItem().getName());
        assertEquals(item.getDescription(), booking.getItem().getDescription());
        assertEquals(item.getAvailable(), booking.getItem().getAvailable());
        assertEquals(bookingAuthor.getId(), booking.getUser().getId());
        assertEquals(bookingAuthor.getName(), booking.getUser().getName());
        assertEquals(bookingAuthor.getEmail(), booking.getUser().getEmail());
    }

    private long getNextId() {
        return nextId++;
    }

    private Item createItem(User owner) {
        var item = new Item();
        var itemId = getNextId();

        item.setId(itemId);
        item.setName("Item " + itemId);
        item.setDescription("Item description " + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Booking createBooking(User author, Item item, LocalDateTime start) {
        var booking = new Booking();
        var bookingId = getNextId();

        booking.setId(bookingId);
        booking.setItem(item);
        booking.setUser(author);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(start);
        booking.setEndTime(start.plusDays(1));
        return booking;
    }

    private BookingRequestDto createIncomingBookingDto(long itemId, LocalDateTime startTime) {
        var bookingDto = new BookingRequestDto();

        bookingDto.setItemId(itemId);
        bookingDto.setStart(startTime);
        bookingDto.setEnd(startTime.plusDays(1));
        return bookingDto;
    }
}