package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @Valid @RequestBody BookingRequestDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable("bookingId") long bookingId,
                                    @RequestParam("approved") boolean approvedStatus) {
        return bookingClient.setBookingStatus(userId, bookingId, approvedStatus);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("bookingId") long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return bookingClient.getAllBookingsByUserAndState(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return bookingClient.getAllBookingsByOwnerAndState(ownerId, bookingState, from, size);
    }
}
