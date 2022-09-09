package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingTimeConflictsException;
import ru.practicum.shareit.booking.exceptions.IllegalBookingApproveException;
import ru.practicum.shareit.booking.exceptions.ItemBookedByItsOwnerException;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingControllerTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private long nextId = 1;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Test
    void createNewBookingReturnStatus200AndNewBooking() throws Exception {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingAuthor = createUser();
        var creatingTime = LocalDateTime.now().plusHours(1);
        var outgoingBookingDto = createOutgoingBookingDto(bookingAuthor, item, creatingTime);
        var incomingBookingDto = createIncomingBookingDto(item.getId(), creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(bookingAuthor.getId()),
                Mockito.any(BookingIncomingDto.class)))
                .thenReturn(outgoingBookingDto);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingAuthor.getId())
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(outgoingBookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(outgoingBookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.requestId").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.comments").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(outgoingBookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(outgoingBookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()));
    }

    @Test
    void createNewBookingReturnStatus404WhenUserIdIsNotValid() throws Exception {
        var errMsg = "Пользователь не найден";
        var creatingTime = LocalDateTime.now().plusHours(1);
        var incomingBookingDto = createIncomingBookingDto(1L, creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(1L), Mockito.any(BookingIncomingDto.class)))
                        .thenThrow(new UserNotFoundException(errMsg));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewBookingReturnStatus404WhenItemIdIsNotValid() throws Exception {
        var errMsg = "Вещь не найдена";
        var creatingTime = LocalDateTime.now().plusHours(1);
        var incomingBookingDto = createIncomingBookingDto(1L, creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(1L), Mockito.any(BookingIncomingDto.class)))
                        .thenThrow(new ItemNotFoundException(errMsg));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewBookingReturnStatus404WhenUserIsItemOwner() throws Exception {
        var errMsg = "Пользователь с id = 1 владелец вещи с id = 1";
        var creatingTime = LocalDateTime.now().plusHours(1);
        var incomingBookingDto = createIncomingBookingDto(1L, creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(1L), Mockito.any(BookingIncomingDto.class)))
                        .thenThrow(new ItemBookedByItsOwnerException(errMsg));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewBookingReturnStatus400WhenItemIsNotAvailable() throws Exception {
        var errMsg = "Элемент с id = 1 недоступен.";
        var creatingTime = LocalDateTime.now().plusHours(1);
        var incomingBookingDto = createIncomingBookingDto(1L, creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(1L), Mockito.any(BookingIncomingDto.class)))
                        .thenThrow(new ItemBookedByItsOwnerException(errMsg));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewBookingReturnStatus400WhenTimeConflictIsOccur() throws Exception {
        var errMsg = "Конфликт времени начала/окончания для элемента с id = 1";
        var creatingTime = LocalDateTime.now().plusHours(1);
        var incomingBookingDto = createIncomingBookingDto(1L, creatingTime);

        Mockito.when(bookingService.createBooking(Mockito.eq(1L), Mockito.any(BookingIncomingDto.class)))
                        .thenThrow(new BookingTimeConflictsException(errMsg));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void setBookingStatusReturnStatus200AndBooking() throws Exception {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingAuthor = createUser();
        var creatingTime = LocalDateTime.now().plusHours(1);
        var outgoingBookingDto = createOutgoingBookingDto(bookingAuthor, item, creatingTime);

        outgoingBookingDto.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.setBookingStatus(itemOwner.getId(), 1, true))
                .thenReturn(outgoingBookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", itemOwner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(outgoingBookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(outgoingBookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.requestId").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.comments").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(outgoingBookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(outgoingBookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()));
    }

    @Test
    void setBookingStatusReturnStatus404WhenBookingIdIsIllegal() throws Exception {
        var errMsg = "Бронирование с id = 1 и владельцем с id = 1 не найдено";

        Mockito.when(bookingService.setBookingStatus(1, 1, true))
                .thenThrow(new BookingNotFoundException(errMsg));
        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void setBookingStatusReturnStatus400WhenBookingHasIllegalStatus() throws Exception {
        var errMsg = "Статус бронирования с id = 1 не WAITING";

        Mockito.when(bookingService.setBookingStatus(1, 1, true))
                .thenThrow(new IllegalBookingApproveException(errMsg));
        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getBookingByIdReturnStatus200AndBooking() throws Exception {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingAuthor = createUser();
        var creatingTime = LocalDateTime.now().plusHours(1);
        var outgoingBookingDto = createOutgoingBookingDto(bookingAuthor, item, creatingTime);

        Mockito.when(bookingService.getBookingById(1, 1)).thenReturn(outgoingBookingDto);

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(outgoingBookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(outgoingBookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.requestId").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.item.comments").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(outgoingBookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(outgoingBookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()));
    }

    @Test
    void getBookingsByUserAndStateReturnStatus200AndBooking() throws Exception {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingAuthor = createUser();
        var creatingTime = LocalDateTime.now().plusHours(1);
        var outgoingBookingDto = createOutgoingBookingDto(bookingAuthor, item, creatingTime);

        Mockito.when(bookingService.getAllBookingsByUserAndState(1, BookingState.WAITING, 0, 2))
                .thenReturn(List.of(outgoingBookingDto));

        mvc.perform(get("/bookings?state=WAITING&from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$[0].item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(outgoingBookingDto.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(outgoingBookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(outgoingBookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.requestId").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.comments").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(outgoingBookingDto.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(outgoingBookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].start").value(outgoingBookingDto.getStart()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end").value(outgoingBookingDto.getEnd()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(outgoingBookingDto.getStatus().toString()));
    }

    @Test
    void getBookingsByOwnerAndStateReturnStatus200AndBooking() throws Exception {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var bookingAuthor = createUser();
        var creatingTime = LocalDateTime.now().plusHours(1);
        var outgoingBookingDto = createOutgoingBookingDto(bookingAuthor, item, creatingTime);

        Mockito.when(bookingService.getAllBookingsByOwnerAndState(1, BookingState.WAITING, 0, 2))
                .thenReturn(List.of(outgoingBookingDto));

        mvc.perform(get("/bookings/owner?state=WAITING&from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$[0].item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(outgoingBookingDto.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(outgoingBookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(outgoingBookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.requestId").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].item.comments").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(outgoingBookingDto.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(outgoingBookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].start").value(outgoingBookingDto.getStart()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end").value(outgoingBookingDto.getEnd()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(outgoingBookingDto.getStatus().toString()));
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

    private BookingOutgoingDto createOutgoingBookingDto(User author, Item item, LocalDateTime start) {
        var booking = new Booking();
        var bookingId = getNextId();

        booking.setId(bookingId);
        booking.setItem(item);
        booking.setUser(author);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(start);
        booking.setEndTime(start.plusDays(1));
        return BookingMapper.mapBookingToDto(booking);
    }

    private BookingIncomingDto createIncomingBookingDto(Long itemId, LocalDateTime startTime) {
        var bookingDto = new BookingIncomingDto();

        bookingDto.setItemId(itemId);
        bookingDto.setStart(startTime);
        bookingDto.setEnd(startTime.plusDays(1));
        return bookingDto;
    }
}