package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutgoingDto createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @Valid @RequestBody BookingIncomingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutgoingDto setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable("bookingId") long bookingId,
                                    @RequestParam("approved") boolean approvedStatus) {
        return bookingService.setBookingStatus(userId, bookingId, approvedStatus);
    }

    @GetMapping("/{bookingId}")
    public BookingOutgoingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingOutgoingDto> getBookingsByUserAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return bookingService.getAllBookingsByUserAndState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingOutgoingDto> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return bookingService.getAllBookingsByOwnerAndState(ownerId, bookingState, from, size);
    }
}
