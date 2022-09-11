package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody BookingRequestDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable("bookingId") long bookingId,
                                       @RequestParam("approved") boolean approvedStatus) {
        return bookingService.setBookingStatus(userId, bookingId, approvedStatus);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingsByUserAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state") BookingState bookingState,
            @RequestParam(name = "from") long from,
            @RequestParam(name = "size") int size) {
        return bookingService.getAllBookingsByUserAndState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state") BookingState bookingState,
            @RequestParam(name = "from") long from,
            @RequestParam(name = "size") int size) {
        return bookingService.getAllBookingsByOwnerAndState(ownerId, bookingState, from, size);
    }
}
