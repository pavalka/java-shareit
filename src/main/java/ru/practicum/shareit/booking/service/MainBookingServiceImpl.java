package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainBookingServiceImpl implements MainBookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public BookingOutgoingDto getBookingById(long userId, long bookingId) {
        var user = userService.getUserById(userId);

        return BookingMapper.mapBookingToDto(bookingService.getBookingById(user, bookingId));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<BookingOutgoingDto> getAllBookingsByUserAndState(long userId, BookingState bookingState) {
        var user = userService.getUserById(userId);

        return BookingMapper.mapBookingCollectionToDto(bookingService.getAllBookingsByUserAndState(user, bookingState));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<BookingOutgoingDto> getAllBookingsByOwnerAndState(long ownerId, BookingState bookingState) {
        var owner = userService.getUserById(ownerId);

        return BookingMapper.mapBookingCollectionToDto(bookingService.getAllBookingsByOwnerAndState(owner,
                bookingState));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingOutgoingDto createBooking(long userId, BookingIncomingDto bookingDto) {
        var user = userService.getUserById(userId);
        var item = itemService.getItemById(bookingDto.getItemId());
        var booking = BookingMapper.mapDtoToBooking(bookingDto,item, user);

        return BookingMapper.mapBookingToDto(bookingService.createBooking(booking));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingOutgoingDto setBookingStatus(long ownerId, long bookingId, boolean approved) {
        var owner = userService.getUserById(ownerId);

        return BookingMapper.mapBookingToDto(bookingService.setBookingStatus(owner, bookingId, approved));
    }
}
