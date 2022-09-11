package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingNotAvailableItemException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingTimeConflictsException;
import ru.practicum.shareit.booking.exceptions.IllegalBookingApproveException;
import ru.practicum.shareit.booking.exceptions.ItemBookedByItsOwnerException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    private long nextId = 1;

    @Mock
    private UserDao userRepository;

    @Mock
    private ItemDao itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void getBookingByIdThrowsExceptionWhenUserIdIsIllegal() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(10, 1));
        assertEquals(errMsg, ex.getMessage());

        Mockito.verify(bookingRepository, Mockito.never()).findByIdAndUserOrOwner(Mockito.anyLong(),
                Mockito.any(User.class));
    }

    @Test
    void getBookingByIdThrowsExceptionWhenNoBookingForUser() {
        var bookingAuthor = createUser();
        var bookingId = 10;
        var errMsg = String.format("Бронирование с id = %d для пользователя с id = %d не найдено.", bookingId,
                bookingAuthor.getId());

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findByIdAndUserOrOwner(bookingId, bookingAuthor))
                .thenReturn(Optional.empty());

        var ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(bookingAuthor.getId(), bookingId));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void getBookingByIdReturnBooking() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findByIdAndUserOrOwner(booking.getId(), bookingAuthor))
                .thenReturn(Optional.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getBookingById(bookingAuthor.getId(),
                booking.getId()));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findByIdAndUserOrOwner(booking.getId(), bookingAuthor);
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsAll() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUser(Mockito.eq(bookingAuthor), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.ALL, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUser(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsPast() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUserAndStateIsPast(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.PAST, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUserAndStateIsPast(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsFuture() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUserAndStateIsFuture(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.FUTURE, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUserAndStateIsFuture(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsCurrent() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUserAndStateIsCurrent(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.CURRENT, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUserAndStateIsCurrent(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsWaiting() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUserAndStatus(Mockito.eq(bookingAuthor),
                        Mockito.eq(BookingStatus.WAITING), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.WAITING, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUserAndStatus(Mockito.eq(bookingAuthor),
                Mockito.eq(BookingStatus.WAITING), Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateReturnItemWithStateEqualsRejected() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByUserAndStatus(Mockito.eq(bookingAuthor),
                        Mockito.eq(BookingStatus.REJECTED), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByUserAndState(bookingAuthor.getId(),
                BookingState.REJECTED, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByUserAndStatus(Mockito.eq(bookingAuthor),
                Mockito.eq(BookingStatus.REJECTED), Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserAndStateThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingsByUserAndState(10, BookingState.ALL, 0, 2));
        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsAll() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByItemOwner(Mockito.eq(bookingAuthor), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.ALL, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemOwner(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsPast() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByOwnerAndStateIsPast(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.PAST, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByOwnerAndStateIsPast(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsFuture() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByOwnerAndStateIsFuture(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.FUTURE, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByOwnerAndStateIsFuture(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsCurrent() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByOwnerAndStateIsCurrent(Mockito.eq(bookingAuthor),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.CURRENT, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByOwnerAndStateIsCurrent(Mockito.eq(bookingAuthor),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsWaiting() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByItemOwnerAndStatus(Mockito.eq(bookingAuthor),
                        Mockito.eq(BookingStatus.WAITING), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.WAITING, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemOwnerAndStatus(Mockito.eq(bookingAuthor),
                Mockito.eq(BookingStatus.WAITING), Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateReturnItemWithStateEqualsRejected() {
        var itemOwner = createUser();
        var bookingAuthor = createUser();
        var item = createItem(itemOwner);
        var startTime = LocalDateTime.now();
        var booking = createBooking(bookingAuthor, item, startTime);

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(bookingRepository.findAllByItemOwnerAndStatus(Mockito.eq(bookingAuthor),
                        Mockito.eq(BookingStatus.REJECTED), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.getAllBookingsByOwnerAndState(bookingAuthor.getId(),
                BookingState.REJECTED, 0, 2).toArray(new BookingDto[1]));

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
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemOwnerAndStatus(Mockito.eq(bookingAuthor),
                Mockito.eq(BookingStatus.REJECTED), Mockito.any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerAndStateThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingsByOwnerAndState(10, BookingState.ALL, 0, 2));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void createBookingThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(10, createIncomingBookingDto(1, LocalDateTime.now())));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingThrowsExceptionWhenItemIdIsInvalid() {
        var bookingDto = createIncomingBookingDto(10, LocalDateTime.now());
        var errMsg = "Элемент с id = 10 не найден";
        var bookingAuthor = createUser();

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(bookingAuthor.getId(), bookingDto));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingThrowsExceptionWhenUserIsOwnerOfItem() {
        var bookingAuthor = createUser();
        var item = createItem(bookingAuthor);
        var bookingDto = createIncomingBookingDto(item.getId(), LocalDateTime.now());
        var errMsg = String.format("Пользователь с id = %d владелец вещи с id = %d", bookingAuthor.getId(),
                bookingDto.getItemId());

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        var ex = assertThrows(ItemBookedByItsOwnerException.class,
                () -> bookingService.createBooking(bookingAuthor.getId(), bookingDto));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingThrowsExceptionWhenItemIsNotAvailable() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingDto = createIncomingBookingDto(item.getId(), LocalDateTime.now());
        var errMsg = String.format("Элемент с id = %d недоступен.", item.getId());

        item.setAvailable(false);
        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        var ex = assertThrows(BookingNotAvailableItemException.class,
                () -> bookingService.createBooking(bookingAuthor.getId(), bookingDto));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingCreateBooking() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingDto = createIncomingBookingDto(item.getId(), LocalDateTime.now());

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findAllByItemAndTimeConflicts(item, bookingDto.getStart(), bookingDto.getEnd()))
                .thenReturn(List.of());

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> {
                    var booking = invocation.<Booking>getArgument(0);
                    booking.setId(getNextId());
                    return booking;
                });

        var outgoingBookingDto = assertDoesNotThrow(() -> bookingService.createBooking(bookingAuthor.getId(),
                bookingDto));

        assertEquals(item.getId(), outgoingBookingDto.getItem().getId());
        assertEquals(item.getName(), outgoingBookingDto.getItem().getName());
        assertEquals(item.getDescription(), outgoingBookingDto.getItem().getDescription());
        assertEquals(item.getAvailable(), outgoingBookingDto.getItem().getAvailable());
        assertNull(outgoingBookingDto.getItem().getLastBooking());
        assertNull(outgoingBookingDto.getItem().getNextBooking());
        assertNull(outgoingBookingDto.getItem().getComments());
        assertNull(outgoingBookingDto.getItem().getRequestId());
        assertEquals(bookingAuthor.getId(), outgoingBookingDto.getBooker().getId());
        assertEquals(bookingAuthor.getName(), outgoingBookingDto.getBooker().getName());
        assertEquals(bookingAuthor.getEmail(), outgoingBookingDto.getBooker().getEmail());
        assertEquals(bookingDto.getStart(), outgoingBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), outgoingBookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, outgoingBookingDto.getStatus());
    }

    @Test
    void createBookingThrowsExceptionWhenBookingHasTimeConflict() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingDto = createIncomingBookingDto(item.getId(), LocalDateTime.now());
        var booking = createBooking(bookingAuthor, item, bookingDto.getStart());
        var errMsg = String.format("Конфликт времени начала/окончания для элемента с id = %d", item.getId());

        Mockito.when(userRepository.findById(bookingAuthor.getId()))
                .thenReturn(Optional.of(bookingAuthor));

        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findAllByItemAndTimeConflicts(item, bookingDto.getStart(), bookingDto.getEnd()))
                .thenReturn(List.of(booking));

        var ex = assertThrows(BookingTimeConflictsException.class,
                () -> bookingService.createBooking(bookingAuthor.getId(), bookingDto));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void setBookingStatusThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.setBookingStatus(10, 1, true));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void setBookingStatusThrowsExceptionWhenBookingIsApprovedByUserIsNotItemOwner() {
        long bookingId = 1;
        var user = createUser();
        var errMsg = String.format("Бронирование с id = %d и владельцем с id = %d не найдено", bookingId,
                user.getId());

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(bookingRepository.findByIdAndItemOwner(bookingId, user))
                .thenReturn(Optional.empty());

        var ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.setBookingStatus(user.getId(), bookingId, true));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void setBookingStatusThrowsExceptionWhenBookingStatusIsNotWaiting() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var booking = createBooking(bookingAuthor, item, LocalDateTime.now());
        var errMsg = String.format("Статус бронирования с id = %d не WAITING", booking.getId());

        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(bookingRepository.findByIdAndItemOwner(booking.getId(), itemOwner))
                .thenReturn(Optional.of(booking));

        var ex = assertThrows(IllegalBookingApproveException.class,
                () -> bookingService.setBookingStatus(itemOwner.getId(), booking.getId(), true));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void setBookingStatusSetStatusApprovedAndReturnBookingWhenOwnerIsApprovingBooking() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var booking = createBooking(bookingAuthor, item, LocalDateTime.now());

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(bookingRepository.findByIdAndItemOwner(booking.getId(), itemOwner))
                .thenReturn(Optional.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.setBookingStatus(itemOwner.getId(), booking.getId(),
                true));

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
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void setBookingStatusSetStatusRejectedAndReturnBookingWhenOwnerIsRejectingBooking() {
        var bookingAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var booking = createBooking(bookingAuthor, item, LocalDateTime.now());

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(bookingRepository.findByIdAndItemOwner(booking.getId(), itemOwner))
                .thenReturn(Optional.of(booking));

        var bookingDto = assertDoesNotThrow(() -> bookingService.setBookingStatus(itemOwner.getId(), booking.getId(),
                false));

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
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
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