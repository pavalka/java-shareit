package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplTest {
    private long nextNum = 1;
    private final UserDao userRepository;
    private final ItemDao itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingServiceImpl bookingService;

    @AfterEach
    void clearDb() {
        userRepository.deleteAll();
    }

    @Test
    void getBookingById() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(5)));

        var result = assertDoesNotThrow(() -> bookingService.getBookingById(bookingAuthorOne.getId(), booking.getId()));

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getItem().getName(), result.getItem().getName());
        assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
        assertEquals(booking.getUser().getId(), result.getBooker().getId());
        assertEquals(booking.getUser().getName(), result.getBooker().getName());
        assertEquals(booking.getUser().getEmail(), result.getBooker().getEmail());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getStartTime(), result.getStart());
        assertEquals(booking.getEndTime(), result.getEnd());
    }

    @Test
    void getAllBookingsByUserAndState() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusHours(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthorOne.getId(),
                BookingState.CURRENT, 0, 3).toArray(new BookingDto[1]));

        assertEquals(2, result.length);
        assertEquals(bookingTwo.getId(), result[0].getId());
        assertEquals(bookingTwo.getItem().getId(), result[0].getItem().getId());
        assertEquals(bookingTwo.getItem().getName(), result[0].getItem().getName());
        assertEquals(bookingTwo.getItem().getDescription(), result[0].getItem().getDescription());
        assertEquals(bookingTwo.getItem().getAvailable(), result[0].getItem().getAvailable());
        assertEquals(bookingTwo.getUser().getId(), result[0].getBooker().getId());
        assertEquals(bookingTwo.getUser().getName(), result[0].getBooker().getName());
        assertEquals(bookingTwo.getUser().getEmail(), result[0].getBooker().getEmail());
        assertEquals(bookingTwo.getStatus(), result[0].getStatus());
        assertEquals(bookingTwo.getStartTime(), result[0].getStart());
        assertEquals(bookingTwo.getEndTime(), result[0].getEnd());
        assertEquals(bookingOne.getId(), result[1].getId());
        assertEquals(bookingOne.getItem().getId(), result[1].getItem().getId());
        assertEquals(bookingOne.getItem().getName(), result[1].getItem().getName());
        assertEquals(bookingOne.getItem().getDescription(), result[1].getItem().getDescription());
        assertEquals(bookingOne.getItem().getAvailable(), result[1].getItem().getAvailable());
        assertEquals(bookingOne.getUser().getId(), result[1].getBooker().getId());
        assertEquals(bookingOne.getUser().getName(), result[1].getBooker().getName());
        assertEquals(bookingOne.getUser().getEmail(), result[1].getBooker().getEmail());
        assertEquals(bookingOne.getStatus(), result[1].getStatus());
        assertEquals(bookingOne.getStartTime(), result[1].getStart());
        assertEquals(bookingOne.getEndTime(), result[1].getEnd());
    }

    @Test
    void getAllBookingsByOwnerAndState() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));
        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().minusHours(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(itemOwnerOne.getId(),
                BookingState.CURRENT, 0, 4).toArray(new BookingDto[1]));

        assertEquals(2, result.length);
        assertEquals(bookingTwo.getId(), result[0].getId());
        assertEquals(bookingTwo.getItem().getId(), result[0].getItem().getId());
        assertEquals(bookingTwo.getItem().getName(), result[0].getItem().getName());
        assertEquals(bookingTwo.getItem().getDescription(), result[0].getItem().getDescription());
        assertEquals(bookingTwo.getItem().getAvailable(), result[0].getItem().getAvailable());
        assertEquals(bookingTwo.getUser().getId(), result[0].getBooker().getId());
        assertEquals(bookingTwo.getUser().getName(), result[0].getBooker().getName());
        assertEquals(bookingTwo.getUser().getEmail(), result[0].getBooker().getEmail());
        assertEquals(bookingTwo.getStatus(), result[0].getStatus());
        assertEquals(bookingTwo.getStartTime(), result[0].getStart());
        assertEquals(bookingTwo.getEndTime(), result[0].getEnd());
        assertEquals(bookingOne.getId(), result[1].getId());
        assertEquals(bookingOne.getItem().getId(), result[1].getItem().getId());
        assertEquals(bookingOne.getItem().getName(), result[1].getItem().getName());
        assertEquals(bookingOne.getItem().getDescription(), result[1].getItem().getDescription());
        assertEquals(bookingOne.getItem().getAvailable(), result[1].getItem().getAvailable());
        assertEquals(bookingOne.getUser().getId(), result[1].getBooker().getId());
        assertEquals(bookingOne.getUser().getName(), result[1].getBooker().getName());
        assertEquals(bookingOne.getUser().getEmail(), result[1].getBooker().getEmail());
        assertEquals(bookingOne.getStatus(), result[1].getStatus());
        assertEquals(bookingOne.getStartTime(), result[1].getStart());
        assertEquals(bookingOne.getEndTime(), result[1].getEnd());
    }

    @Test
    void createBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var creatingTime = LocalDateTime.now();

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        var bookingDto = createIncomingBookingDto(itemOne.getId(), creatingTime.plusDays(2));
        var result = assertDoesNotThrow(() -> bookingService.createBooking(bookingAuthorTwo.getId(), bookingDto));

        assertEquals(itemOne.getId(), result.getItem().getId());
        assertEquals(itemOne.getName(), result.getItem().getName());
        assertEquals(itemOne.getDescription(), result.getItem().getDescription());
        assertEquals(itemOne.getAvailable(), result.getItem().getAvailable());
        assertEquals(bookingAuthorTwo.getId(), result.getBooker().getId());
        assertEquals(bookingAuthorTwo.getName(), result.getBooker().getName());
        assertEquals(bookingAuthorTwo.getEmail(), result.getBooker().getEmail());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }

    @Test
    void setBookingStatus() {
        var bookingAuthorOne = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var creatingTime = LocalDateTime.now();

        var waitingBooking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        assertEquals(BookingStatus.WAITING, waitingBooking.getStatus());

        var approvedBooking = assertDoesNotThrow(() -> bookingService.setBookingStatus(itemOwnerOne.getId(),
                waitingBooking.getId(), true));

        assertEquals(waitingBooking.getId(), approvedBooking.getId());
        assertEquals(waitingBooking.getItem().getId(), approvedBooking.getItem().getId());
        assertEquals(waitingBooking.getItem().getName(), approvedBooking.getItem().getName());
        assertEquals(waitingBooking.getItem().getDescription(), approvedBooking.getItem().getDescription());
        assertEquals(waitingBooking.getItem().getAvailable(), approvedBooking.getItem().getAvailable());
        assertEquals(waitingBooking.getUser().getId(), approvedBooking.getBooker().getId());
        assertEquals(waitingBooking.getUser().getName(), approvedBooking.getBooker().getName());
        assertEquals(waitingBooking.getUser().getEmail(), approvedBooking.getBooker().getEmail());
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
        assertEquals(waitingBooking.getStartTime(), approvedBooking.getStart());
        assertEquals(waitingBooking.getEndTime(), approvedBooking.getEnd());
    }

    private long getNextNum() {
        return nextNum++;
    }

    private Item createItem(User owner) {
        var item = new Item();
        var itemId = getNextNum();

        item.setName("Item " + itemId);
        item.setDescription("Item description " + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextNum();

        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Booking createBooking(User author, Item item, LocalDateTime start) {
        var booking = new Booking();

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