package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
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
    public BookingDto getBookingById(long userId, long bookingId) {
        var user = getUserById(userId);
        var booking = bookingRepository.findByIdAndUserOrOwner(bookingId, user).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id = %d для пользователя с id = %d " +
                        "не найдено.", bookingId, user.getId())));

        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllBookingsByUserAndState(long userId,
                                                               @NonNull BookingState bookingState,
                                                               long from,
                                                               int size) {
        var user = getUserById(userId);
        var pageable = new PageableByOffsetAndSize(from, size, Sort.by(Sort.Direction.DESC, "startTime"));

        switch (bookingState) {
            case ALL:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUser(user, pageable));

            case PAST:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsPast(user,
                        pageable));

            case FUTURE:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsFuture(user,
                        pageable));

            case CURRENT:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStateIsCurrent(user,
                        pageable));

            case WAITING:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStatus(user,
                        BookingStatus.WAITING, pageable));

            case REJECTED:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByUserAndStatus(user,
                        BookingStatus.REJECTED, pageable));

            default:
                return new ArrayList<>();
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByOwnerAndState(long ownerId,
                                                                @NonNull BookingState bookingState,
                                                                long from, int size) {
        var owner = getUserById(ownerId);
        var pageable = new PageableByOffsetAndSize(from, size, Sort.by(Sort.Direction.DESC, "startTime"));

        switch (bookingState) {
            case ALL:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwner(owner, pageable));

            case PAST:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsPast(owner,
                        pageable));

            case FUTURE:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsFuture(owner,
                        pageable));

            case CURRENT:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByOwnerAndStateIsCurrent(owner,
                        pageable));

            case WAITING:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwnerAndStatus(owner,
                        BookingStatus.WAITING, pageable));

            case REJECTED:
                return BookingMapper.mapBookingCollectionToDto(bookingRepository.findAllByItemOwnerAndStatus(owner,
                        BookingStatus.REJECTED, pageable));

            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public BookingDto createBooking(long userId, @NonNull BookingRequestDto bookingDto) {
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
    public BookingDto setBookingStatus(long ownerId, long bookingId, boolean approved) {
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
