package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
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

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public BookingOutgoingDto getBookingById(long userId, long bookingId) {
        var user = getUserById(userId);
        var booking = bookingRepository.findByIdAndUserOrOwner(bookingId, user).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id = %d для пользователя с id = %d " +
                        "не найдено.", bookingId, user.getId())));

        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public Collection<BookingOutgoingDto> getAllBookingsByUserAndState(long userId,
                                                                       @NonNull BookingState bookingState) {
        var user = getUserById(userId);
        var sort = Sort.by(Sort.Direction.DESC, "startTime");

        switch (bookingState) {
            case ALL:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUser(user, sort));

            case PAST:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsPast(user,
                        sort));

            case FUTURE:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsFuture(user,
                        sort));

            case CURRENT:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsCurrent(user,
                        sort));

            case WAITING:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStatus(user,
                        BookingStatus.WAITING, sort));

            case REJECTED:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStatus(user,
                        BookingStatus.REJECTED, sort));

            default:
                return new ArrayList<>();
        }
    }

    @Override
    public Collection<BookingOutgoingDto> getAllBookingsByOwnerAndState(long ownerId,
                                                                        @NonNull BookingState bookingState) {
        var owner = getUserById(ownerId);
        var sort = Sort.by(Sort.Direction.DESC, "startTime");

        switch (bookingState) {
            case ALL:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwner(owner, sort));

            case PAST:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsPast(owner,
                        sort));

            case FUTURE:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsFuture(owner,
                        sort));

            case CURRENT:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsCurrent(owner,
                        sort));

            case WAITING:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwnerAndStatus(owner,
                        BookingStatus.WAITING, sort));

            case REJECTED:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwnerAndStatus(owner,
                        BookingStatus.REJECTED, sort));

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public BookingOutgoingDto createBooking(long userId, @NonNull BookingIncomingDto bookingDto) {
        var user = getUserById(userId);
        var item = getItemById(bookingDto.getItemId());
        var booking = BookingMapper.mapDtoToBooking(bookingDto, item, user);

        if (user.equals(item.getOwner())) {
            throw new ItemBookedByItsOwnerException(String.format("Пользователь с id = %d владелец вещи с id = %d",
                    user.getId(), item.getId()));
        }
        if (!item.getAvailable()) {
            throw new BookingNotAvailableItemException(String.format("Элемент с id = %d недоступен.",
                    item.getId()));
        }
        if (checkBookingTimeConflicts(booking)) {
            throw new BookingTimeConflictsException(String.format("Конфликт времени начала/окончания для " +
                    "элемента с id = %d", booking.getItem().getId()));
        }
        return BookingMapper.mapBookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutgoingDto setBookingStatus(long ownerId, long bookingId, boolean approved) {
        var owner = getUserById(ownerId);
        var booking = bookingRepository.findByIdAndItemOwner(bookingId, owner).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id = %d и владельцем с id = %d не найдено",
                        bookingId, ownerId)));

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalBookingApproveException(String.format("Статус бронирования с id = %d не WAITING",
                  bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapBookingToDto(booking);
    }

    private boolean checkBookingTimeConflicts(Booking booking) {
        return bookingRepository.findAllByItemAndTimeConflicts(booking.getItem(), booking.getStartTime(),
                booking.getEndTime()).size() != 0;
    }

    private User getUserById(long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    private Item getItemById(long itemId) {
        return itemDao.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Элемент с id = %d не найден", itemId)));
    }
}
