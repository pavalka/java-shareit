package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exceptions.BookingNotAvailableItemException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingTimeConflictsException;
import ru.practicum.shareit.booking.exceptions.IllegalBookingApproveException;
import ru.practicum.shareit.booking.exceptions.ItemBookedByItsOwnerException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Booking getBookingById(long userId, long bookingId) {
        var user = userService.getUserById(userId);

        return bookingRepository.findByIdAndUserOrOwner(bookingId, user).orElseThrow(() -> new BookingNotFoundException(
                String.format("Бронирование с id = %d для пользователя с id = %d не найдено.", bookingId, userId)));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Booking> getAllBookingsByUserAndState(long userId, @NonNull BookingState bookingState) {
        var user = userService.getUserById(userId);

        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByUserOrderByStartTimeDesc(user);

            case PAST:
                return bookingRepository.findAllByUserAndStateIsPastOrderByStartTimeDesc(user);

            case FUTURE:
                return bookingRepository.findAllByUserAndStateIsFutureOrderByStartTimeDesc(user);

            case CURRENT:
                return bookingRepository.findAllByUserAndStateIsCurrentOrderByStartTimeDesc(user);

            case WAITING:
                return bookingRepository.findAllByUserAndStatusOrderByStartTimeDesc(user, BookingStatus.WAITING);

            case REJECTED:
                return bookingRepository.findAllByUserAndStatusOrderByStartTimeDesc(user, BookingStatus.REJECTED);

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Booking> getAllBookingsByOwnerAndState(long ownerId, @NonNull BookingState bookingState) {
        var user = userService.getUserById(ownerId);

        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByItemOwnerOrderByStartTimeDesc(user);

            case PAST:
                return bookingRepository.findAllByOwnerAndStateIsPastOrderByStartTimeDesc(user);

            case FUTURE:
                return bookingRepository.findAllByOwnerAndStateIsFutureOrderByStartTimeDesc(user);

            case CURRENT:
                return bookingRepository.findAllByOwnerAndStateIsCurrentOrderByStartTimeDesc(user);

            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartTimeDesc(user, BookingStatus.WAITING);

            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartTimeDesc(user, BookingStatus.REJECTED);

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking createBooking(long userId, long itemId, @NonNull LocalDateTime startTime,
                                 @NonNull LocalDateTime endTime) {
        var user = userService.getUserById(userId);
        var item = itemService.getItemById(itemId);

        if (user.equals(item.getOwner())) {
            throw new ItemBookedByItsOwnerException(String.format("Пользователь с id = %d владелец вещи с id = %d",
                    userId, itemId));
        }

        if (!item.getAvailable()) {
            throw new BookingNotAvailableItemException(String.format("Элемент с id = %d недоступен.", itemId));
        }

        if (checkBookingTimeConflicts(item, startTime, endTime)) {
            throw new BookingTimeConflictsException(String.format("Конфликт времени начала/окончания для " +
                    "элемента с id = %d", itemId));
        }

        var booking = new Booking();

        booking.setUser(user);
        booking.setItem(item);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking setBookingStatus(long ownerId, long bookingId, boolean approved) {
        var booking = getBookingByIdAndOwner(ownerId, bookingId);

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalBookingApproveException(String.format("Статус бронирования с id = %d не WAITING",
                  bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Booking getBookingByIdAndOwner(long userId, long bookingId) {
        var user = userService.getUserById(userId);

        return bookingRepository.findByIdAndItemOwner(bookingId, user).orElseThrow(() -> new BookingNotFoundException(
                String.format("Бронирование с id = %d и владельцем с id = %d не найдено", bookingId, userId)));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    private boolean checkBookingTimeConflicts(Item item, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findAllByItemAndTimeConflicts(item, startTime, endTime).size() != 0;
    }
}
