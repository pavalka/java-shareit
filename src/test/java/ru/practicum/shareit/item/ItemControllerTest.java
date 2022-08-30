package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BookingToCreateCommentNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

    private long nextItemId = 1;
    private long nextBookingId = 1;

    @Test
    void getItemByIdReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(itemService.getItemByIdAndUser(10L, 1L))
                .thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getItemByIdReturnStatus404WhenItemIdIsInvalid() throws Exception {
        var errMsg = "Элемент с id = 10 не найден";

        Mockito.when(itemService.getItemByIdAndUser(1L, 10L))
                .thenThrow(new ItemNotFoundException(errMsg));

        mvc.perform(get("/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getItemByIdReturnStatus200AndItemDto() throws Exception {
        var itemDto = createItemDto("Item", "description");
        var lastBookingDto = createBookingInfoDto(1);
        var nextBookingDto = createBookingInfoDto(2);

        itemDto.setLastBooking(lastBookingDto);
        itemDto.setNextBooking(nextBookingDto);

        Mockito.when(itemService.getItemByIdAndUser(1L, 1L))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(lastBookingDto.getId()))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(lastBookingDto.getBookerId()))
                .andExpect(jsonPath("$.nextBooking.id").value(nextBookingDto.getId()))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(nextBookingDto.getBookerId()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void getItemsByUserIdReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(itemService.getAllItemsForUser(10, 0, 10))
                .thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getItemsByUserIdReturnStatus400WhenParamFromIsInvalid() throws Exception {
        mvc.perform(get("/items?from=-1&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void getItemsByUserIdReturnStatus200AndItemsDto() throws Exception {
        var itemDto = createItemDto("Item", "description");
        var lastBookingDto = createBookingInfoDto(1);
        var nextBookingDto = createBookingInfoDto(2);

        itemDto.setLastBooking(lastBookingDto);
        itemDto.setNextBooking(nextBookingDto);

        Mockito.when(itemService.getAllItemsForUser(1, 0, 2))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items?from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking.id").value(lastBookingDto.getId()))
                .andExpect(jsonPath("$[0].lastBooking.bookerId").value(lastBookingDto.getBookerId()))
                .andExpect(jsonPath("$[0].nextBooking.id").value(nextBookingDto.getId()))
                .andExpect(jsonPath("$[0].nextBooking.bookerId").value(nextBookingDto.getBookerId()))
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments").isEmpty());
    }

    @Test
    void getItemsByUserIdReturnStatus400WhenParamSizeIsInvalid() throws Exception {
        mvc.perform(get("/items?from=1&size=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createNewItemReturnStatus404WhenUserIdIsNotFound() throws Exception {
        var itemDto = createItemDto("name", "description");
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(itemService.createNewItem(Mockito.eq(10L), Mockito.any(ItemDto.class)))
                .thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewItemReturnStatus200AndItem() throws Exception {
        var itemDto = createItemDto("Item", "description");

        Mockito.when(itemService.createNewItem(Mockito.eq(1L), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void createNewItemReturnStatus400WhenItemNameIsEmpty() throws Exception {
        var itemDto = createItemDto("", "description");

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createNewItemReturnStatus400WhenItemDescriptionIsEmpty() throws Exception {
        var itemDto = createItemDto("item", "");

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createNewItemReturnStatus400WhenItemAvailableIsNull() throws Exception {
        var itemDto = createItemDto("item", "description");

        itemDto.setAvailable(null);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createNewItemReturnStatus400WhenRequestIdIsNotPositive() throws Exception {
        var itemDto = createItemDto("item", "description");

        itemDto.setRequestId(0L);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updateItemReturnStatus404WhenItemIdIsInvalid() throws Exception {
        var errMsg = "Элемент с id = 10 не найден";
        var itemDto = createItemDto("new name", null);

        itemDto.setAvailable(null);
        Mockito.when(itemService.updateItem(Mockito.any(ItemDto.class), Mockito.eq(1L)))
                .thenThrow(new ItemNotFoundException(errMsg));

        mvc.perform(patch("/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void updateItemReturnStatus404WhenUserIsNotItemOwner() throws Exception {
        var itemDto = createItemDto("new name", null);
        var errMsg = "Пользователь с id = 1 не владелец элемента с id = 10";

        itemDto.setAvailable(null);
        Mockito.when(itemService.updateItem(Mockito.any(ItemDto.class), Mockito.eq(1L)))
                .thenThrow(new UserIsNotItemOwnerException(errMsg));

        mvc.perform(patch("/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void updateItemReturnStatus200AndItem() throws Exception {
        var itemDto = createItemDto("item", "description");
        var newItemDto = createItemDto("item", null);

        newItemDto.setAvailable(null);
        Mockito.when(itemService.updateItem(Mockito.any(ItemDto.class), Mockito.eq(1L)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void updateItemReturnStatus400WhenItemNameIsEmpty() throws Exception {
        var itemDto = createItemDto("", null);

        itemDto.setAvailable(null);
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updateItemReturnStatus400WhenItemDescriptionIsEmpty() throws Exception {
        var itemDto = createItemDto(null, "");

        itemDto.setAvailable(null);
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void findItemsByNameAndDescriptionReturnStatus200AndItem() throws Exception {
        var itemDto = createItemDto("Item", "description");

        Mockito.when(itemService.findItemsByNameAndDescription("Tem", 0, 2))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=Tem&from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments").isEmpty());
    }

    @Test
    void findItemsByNameAndDescriptionReturnStatus400WhenParamFromIsNegative() throws Exception {
        mvc.perform(get("/items/search?text=Tem&from=-1&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void findItemsByNameAndDescriptionReturnStatus400WhenParamSizeIsNotPositive() throws Exception {
        mvc.perform(get("/items/search?text=Tem&from=0&size=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createCommentReturnStatus400WhenCreateCommentWithoutBooking() throws Exception {
        var creationTime = LocalDateTime.now();
        var commentDto = createCommentDto("comment", creationTime);
        var errMsg = String.format("Бронирование с параметрами userId = 1, itemId = 1, endTime < %1$tFT%1$tT " +
                "не найдено", creationTime);

        Mockito.when(itemService.createComment(Mockito.eq(1L), Mockito.eq(1L), Mockito.any(CommentDto.class)))
                .thenThrow(new BookingToCreateCommentNotFoundException(errMsg));

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createCommentReturnStatus200AndComment() throws Exception {
        var creationTime = LocalDateTime.now();
        var commentDto = createCommentDto("comment", creationTime);
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Mockito.when(itemService.createComment(Mockito.eq(1L), Mockito.eq(1L), Mockito.any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated()
                        .format(formatter)));
    }

    @Test
    void createCommentReturnStatus400WhenCommentTextIsEmpty() throws Exception {
        var commentDto = createCommentDto("", null);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }



    private long getNextItemId() {
        return nextItemId++;
    }

    private long getNextBookingId() {
        return nextBookingId++;
    }

    private ItemDto createItemDto(String name, String description) {
        var itemDto = new ItemDto();

        itemDto.setId(getNextItemId());
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);
        itemDto.setComments(new ArrayList<>());
        return itemDto;
    }

    private BookingInfoDto createBookingInfoDto(long bookerId) {
        var bookingInfo = new BookingInfoDto();

        bookingInfo.setId(getNextBookingId());
        bookingInfo.setBookerId(bookerId);
        return bookingInfo;
    }

    private CommentDto createCommentDto(String text, LocalDateTime created) {
        var commentDto = new CommentDto();

        commentDto.setId(getNextBookingId());
        commentDto.setText(text);
        commentDto.setCreated(created);
        commentDto.setAuthorName("Author");
        return commentDto;
    }
}