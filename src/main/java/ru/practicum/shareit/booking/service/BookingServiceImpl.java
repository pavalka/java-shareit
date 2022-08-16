package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Booking getBookingById(@NonNull User user, long bookingId) {
        return bookingRepository.findByIdAndUserOrOwner(bookingId, user).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id = %d для пользователя с id = %d " +
                        "не найдено.", bookingId, user.getId())));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Booking> getAllBookingsByUserAndState(@NonNull User user, @NonNull BookingState bookingState) {
        var sort = Sort.by(Sort.Direction.DESC, "startTime");

        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByUser(user, sort);

            case PAST:
                return bookingRepository.findAllByUserAndStateIsPast(user, sort);

            case FUTURE:
                return bookingRepository.findAllByUserAndStateIsFuture(user, sort);

            case CURRENT:
                return bookingRepository.findAllByUserAndStateIsCurrent(user, sort);

            case WAITING:
                return bookingRepository.findAllByUserAndStatus(user, BookingStatus.WAITING, sort);

            case REJECTED:
                return bookingRepository.findAllByUserAndStatus(user, BookingStatus.REJECTED, sort);

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Booking> getAllBookingsByOwnerAndState(@NonNull User owner, @NonNull BookingState bookingState) {
        var sort = Sort.by(Sort.Direction.DESC, "startTime");

        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByItemOwner(owner, sort);

            case PAST:
                return bookingRepository.findAllByOwnerAndStateIsPast(owner, sort);

            case FUTURE:
                return bookingRepository.findAllByOwnerAndStateIsFuture(owner, sort);

            case CURRENT:
                return bookingRepository.findAllByOwnerAndStateIsCurrent(owner, sort);

            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.WAITING, sort);

            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.REJECTED, sort);

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking createBooking(@NonNull Booking booking) {
        if (booking.getUser().equals(booking.getItem().getOwner())) {
            throw new ItemBookedByItsOwnerException(String.format("Пользователь с id = %d владелец вещи с id = %d",
                    booking.getUser().getId(), booking.getItem().getId()));
        }
        if (!booking.getItem().getAvailable()) {
            throw new BookingNotAvailableItemException(String.format("Элемент с id = %d недоступен.",
                    booking.getItem().getId()));
        }
        if (checkBookingTimeConflicts(booking)) {
            throw new BookingTimeConflictsException(String.format("Конфликт времени начала/окончания для " +
                    "элемента с id = %d", booking.getItem().getId()));
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking setBookingStatus(@NonNull User owner, long bookingId, boolean approved) {
        var booking = bookingRepository.findByIdAndItemOwner(bookingId, owner).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id = %d и владельцем с id = %d не найдено",
                        bookingId, owner.getId())));

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

    private boolean checkBookingTimeConflicts(Booking booking) {
        return bookingRepository.findAllByItemAndTimeConflicts(booking.getItem(), booking.getStartTime(),
                booking.getEndTime()).size() != 0;
    }
}
