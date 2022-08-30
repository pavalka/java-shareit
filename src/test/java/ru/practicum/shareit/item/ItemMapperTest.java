package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private long nextUserId = 1;
    private long nextItemId = 1;
    private long nextBookingId = 1;
    private long nextCommentId = 1;
    private long nextRequestId = 1;
    private LocalDateTime nextDateTime = LocalDateTime.of(2022, 8, 25, 10, 23, 0);

    @Test
    void mapItemToItemDtoReturnNullWhenArgumentIsNull() {
        assertNull(ItemMapper.mapItemToItemDto(null));
    }

    @Test
    void mapItemToItemDtoReturnItemDto() {
        var item = getItem();

        var itemDto = ItemMapper.mapItemToItemDto(item);
        var commentsDto = itemDto.getComments().toArray(new CommentDto[1]);

        assertEquals(item.getId(),itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getLastBooking().getId(), itemDto.getLastBooking().getId());
        assertEquals(item.getLastBooking().getUser().getId(), itemDto.getLastBooking().getBookerId());
        assertEquals(item.getNextBooking().getId(), itemDto.getNextBooking().getId());
        assertEquals(item.getNextBooking().getUser().getId(), itemDto.getNextBooking().getBookerId());
        assertEquals(1, commentsDto.length);
        assertEquals(item.getComments().get(0).getId(), commentsDto[0].getId());
        assertEquals(item.getComments().get(0).getUser().getName(), commentsDto[0].getAuthorName());
        assertEquals(item.getComments().get(0).getText(), commentsDto[0].getText());
        assertEquals(item.getComments().get(0).getCreationDate(), commentsDto[0].getCreated());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void mapItemDtoToItemReturnNullWhenArgumentIsNull() {
        assertNull(ItemMapper.mapItemDtoToItem(null, createUser()));
    }

    @Test
    void mapItemDtoToItemReturnItem() {
        var user = createUser();
        var itemDto = createItemDto();
        var item = ItemMapper.mapItemDtoToItem(itemDto, user);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void mapItemDtoToItemOverloadedReturnNullWhenItemDtoArgumentIsNull() {
        var user = createUser();

        assertNull(ItemMapper.mapItemDtoToItem(null, user, createRequest(user)));
    }

    @Test
    void mapItemDtoToItemOverloadedReturnItemDto() {
        var owner = createUser();
        var itemDto = createItemDto();
        var requestAuthor = createUser();
        var request = createRequest(requestAuthor);

        itemDto.setRequestId(request.getId());

        var item = ItemMapper.mapItemDtoToItem(itemDto, owner, request);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    void mapItemsCollectionToItemDtoReturnNullWhenArgumentIsNull() {
        assertNull(ItemMapper.mapItemsCollectionToItemDto(null));
    }

    @Test
    void mapItemsCollectionToItemDtoReturnCollection() {
        var itemOne = getItem();
        var itemTwo = getItem();

        var itemsDto = ItemMapper.mapItemsCollectionToItemDto(List.of(itemOne, itemTwo))
                .toArray(new ItemDto[1]);

        assertEquals(2, itemsDto.length);

        var commentsDto = itemsDto[0].getComments().toArray(new CommentDto[1]);

        assertEquals(itemOne.getId(),itemsDto[0].getId());
        assertEquals(itemOne.getName(), itemsDto[0].getName());
        assertEquals(itemOne.getDescription(), itemsDto[0].getDescription());
        assertEquals(itemOne.getAvailable(), itemsDto[0].getAvailable());
        assertEquals(itemOne.getLastBooking().getId(), itemsDto[0].getLastBooking().getId());
        assertEquals(itemOne.getLastBooking().getUser().getId(), itemsDto[0].getLastBooking().getBookerId());
        assertEquals(itemOne.getNextBooking().getId(), itemsDto[0].getNextBooking().getId());
        assertEquals(itemOne.getNextBooking().getUser().getId(), itemsDto[0].getNextBooking().getBookerId());
        assertEquals(1, commentsDto.length);
        assertEquals(itemOne.getComments().get(0).getId(), commentsDto[0].getId());
        assertEquals(itemOne.getComments().get(0).getUser().getName(), commentsDto[0].getAuthorName());
        assertEquals(itemOne.getComments().get(0).getText(), commentsDto[0].getText());
        assertEquals(itemOne.getComments().get(0).getCreationDate(), commentsDto[0].getCreated());
        assertNull(itemsDto[0].getRequestId());

        commentsDto = itemsDto[1].getComments().toArray(new CommentDto[1]);

        assertEquals(itemTwo.getId(),itemsDto[1].getId());
        assertEquals(itemTwo.getName(), itemsDto[1].getName());
        assertEquals(itemTwo.getDescription(), itemsDto[1].getDescription());
        assertEquals(itemTwo.getAvailable(), itemsDto[1].getAvailable());
        assertEquals(itemTwo.getLastBooking().getId(), itemsDto[1].getLastBooking().getId());
        assertEquals(itemTwo.getLastBooking().getUser().getId(), itemsDto[1].getLastBooking().getBookerId());
        assertEquals(itemTwo.getNextBooking().getId(), itemsDto[1].getNextBooking().getId());
        assertEquals(itemTwo.getNextBooking().getUser().getId(), itemsDto[1].getNextBooking().getBookerId());
        assertEquals(1, commentsDto.length);
        assertEquals(itemTwo.getComments().get(0).getId(), commentsDto[0].getId());
        assertEquals(itemTwo.getComments().get(0).getUser().getName(), commentsDto[0].getAuthorName());
        assertEquals(itemTwo.getComments().get(0).getText(), commentsDto[0].getText());
        assertEquals(itemTwo.getComments().get(0).getCreationDate(), commentsDto[0].getCreated());
        assertNull(itemsDto[1].getRequestId());
    }

    public long getNextUserId() {
        return nextUserId++;
    }

    public long getNextItemId() {
        return nextItemId++;
    }

    public long getNextBookingId() {
        return nextBookingId++;
    }

    public long getNextCommentId() {
        return nextCommentId++;
    }

    public long getNextRequestId() {
        return nextRequestId++;
    }

    public LocalDateTime getNextDateTime() {
        nextDateTime = nextDateTime.plusDays(1);
        return nextDateTime;
    }

    private Item getItem() {
        var bookerOne = createUser();
        var item = createItem(createUser());
        var lastBooking = createBooking(bookerOne, item, getNextDateTime());
        var nextBooking = createBooking(createUser(), item, getNextDateTime());
        var comment = createComment(bookerOne, item, getNextDateTime(), "Text");

        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(List.of(comment));
        return item;
    }

    private Item createItem(User owner) {
        var item = new Item();
        var itemId = getNextItemId();

        item.setId(itemId);
        item.setName("Item " + itemId);
        item.setDescription("Item description " + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextUserId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Booking createBooking(User author, Item item, LocalDateTime start) {
        var booking = new Booking();
        var bookingId = getNextBookingId();

        booking.setId(bookingId);
        booking.setItem(item);
        booking.setUser(author);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(start);
        booking.setEndTime(start.plusDays(1));
        return booking;
    }

    private Comment createComment(User author, Item item, LocalDateTime created, String text) {
        var comment = new Comment();
        var commentId = getNextCommentId();

        comment.setId(commentId);
        comment.setUser(author);
        comment.setItem(item);
        comment.setCreationDate(created);
        comment.setText(text);
        return comment;
    }

    private ItemDto createItemDto() {
        var itemDto = new ItemDto();
        var itemId = getNextItemId();

        itemDto.setId(itemId);
        itemDto.setName("Item " + itemId);
        itemDto.setDescription("Item description " + itemId);
        itemDto.setAvailable(true);
        return itemDto;
    }

    private ItemRequest createRequest(User user) {
        var request = new ItemRequest();
        var requestId = getNextRequestId();

        request.setId(requestId);
        request.setDescription("description " + requestId);
        request.setAuthor(user);
        return request;
    }
}