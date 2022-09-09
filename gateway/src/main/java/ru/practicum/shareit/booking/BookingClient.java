package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long userId, BookingIncomingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> setBookingStatus(long userId, long bookingId, boolean approved) {
        return patch("/{bookingId}?approved={approvedVal}", userId, Map.of("bookingId", bookingId, "approvedVal",
                approved), null);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/{bookingId}", userId, Map.of("bookingId", bookingId));
    }

    public ResponseEntity<Object> getAllBookingsByUserAndState(long userId, BookingState bookingState, long from,
                                                               int size) {
        return get("?state={stateVal}&from={fromVal}&size={sizeVal}", userId, Map.of("stateVal", bookingState,
                "fromVal", from, "sizeVal", size));
    }

    public ResponseEntity<Object> getAllBookingsByOwnerAndState(long ownerId, BookingState bookingState, long from,
                                                                int size) {
        return get("/owner?state={stateVal}&from={fromVal}&size={sizeVal}", ownerId, Map.of("stateVal", bookingState,
                "fromVal", from, "sizeVal", size));
    }
}
