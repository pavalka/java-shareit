package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceImplTest {
    private final UserDao userRepository;
    private final ItemDao itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemServiceImpl itemService;
    private long nextUserId = 1;
    private long nextItemId = 1;

    @AfterEach
    void clearDb() {
        userRepository.deleteAll();
    }

    @Test
    void getAllItemsForUser() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "Item 1", "description 1"));
        var lastBookingAuthor = userRepository.save(createUser());
        var nextBookingAuthor = userRepository.save(createUser());
        var lastBooking = bookingRepository.save(createBooking(lastBookingAuthor, itemOne,
                LocalDateTime.now().minusDays(4)));
        var nextBooking = bookingRepository.save(createBooking(nextBookingAuthor, itemOne,
                LocalDateTime.now().plusDays(3)));
        var comment = commentRepository.save(createComment(lastBookingAuthor, itemOne));

        itemRepository.save(createItem(userTwo, "Item 2", "description 2"));

        var items = assertDoesNotThrow(() -> itemService.getAllItemsForUser(userOne.getId(), 0, 2)
                .toArray(new ItemDto[1]));


        assertEquals(1, items.length);
        assertEquals(itemOne.getId(), items[0].getId());
        assertEquals(itemOne.getName(), items[0].getName());
        assertEquals(itemOne.getDescription(), items[0].getDescription());
        assertEquals(itemOne.getAvailable(), items[0].getAvailable());
        assertEquals(lastBooking.getId(), items[0].getLastBooking().getId());
        assertEquals(lastBookingAuthor.getId(), items[0].getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), items[0].getNextBooking().getId());
        assertEquals(nextBookingAuthor.getId(), items[0].getNextBooking().getBookerId());

        var itemsComments = items[0].getComments().toArray(new CommentDto[1]);

        assertEquals(comment.getId(), itemsComments[0].getId());
        assertEquals(comment.getText(), itemsComments[0].getText());
        assertEquals(comment.getUser().getName(), itemsComments[0].getAuthorName());
        assertEquals(comment.getCreationDate(), itemsComments[0].getCreated());
        assertNull(items[0].getRequestId());
    }

    @Test
    void createNewItem() {
        var user = userRepository.save(createUser());
        var itemDto = createItemDto();

        var createdItem = assertDoesNotThrow(() -> itemService.createNewItem(user.getId(), itemDto));

        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getAvailable(), createdItem.getAvailable());
        assertNull(createdItem.getRequestId());
        assertNull(createdItem.getComments());
        assertNull(createdItem.getLastBooking());
        assertNull(createdItem.getNextBooking());
    }

    @Test
    void updateItem() {
        var user = userRepository.save(createUser());
        var item = itemRepository.save(createItem(user, "Item 1", "description 1"));
        var itemDto = createItemDto();

        itemDto.setId(item.getId());
        itemDto.setAvailable(null);

        var updatedItem = assertDoesNotThrow(() -> itemService.updateItem(itemDto, user.getId()));

        assertEquals(itemDto.getId(), updatedItem.getId());
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(item.getAvailable(), updatedItem.getAvailable());
        assertTrue(updatedItem.getComments().isEmpty());
        assertNull(updatedItem.getRequestId());
        assertNull(updatedItem.getLastBooking());
        assertNull(updatedItem.getNextBooking());
    }

    @Test
    void findItemsByNameAndDescription() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemTwo = createItem(userTwo, "Name", "description of item 2");
        var itemOne = itemRepository.save(createItem(userOne, "Item 1", "description 1"));

        itemTwo.setAvailable(false);
        itemRepository.save(itemTwo);

        itemRepository.save(createItem(userOne, "name", "description of name"));

        var items = assertDoesNotThrow(() -> itemService.findItemsByNameAndDescription("TeM", 0, 4)
                .toArray(new ItemDto[1]));

        assertEquals(1, items.length);
        assertEquals(itemOne.getId(), items[0].getId());
        assertEquals(itemOne.getName(), items[0].getName());
        assertEquals(itemOne.getDescription(), items[0].getDescription());
        assertEquals(itemOne.getAvailable(), items[0].getAvailable());
        assertTrue(items[0].getComments().isEmpty());
        assertNull(items[0].getLastBooking());
        assertNull(items[0].getNextBooking());
        assertNull(items[0].getRequestId());
    }

    @Test
    void getItemByIdAndUser() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "Item 1", "description 1"));
        var lastBookingAuthor = userRepository.save(createUser());
        var nextBookingAuthor = userRepository.save(createUser());
        var lastBooking = bookingRepository.save(createBooking(lastBookingAuthor, itemOne,
                LocalDateTime.now().minusDays(4)));
        var nextBooking = bookingRepository.save(createBooking(nextBookingAuthor, itemOne,
                LocalDateTime.now().plusDays(3)));
        var comment = commentRepository.save(createComment(lastBookingAuthor, itemOne));

        itemRepository.save(createItem(userTwo, "Item 2", "description 2"));

        var item = assertDoesNotThrow(() -> itemService.getItemByIdAndUser(userOne.getId(), itemOne.getId()));

        assertEquals(itemOne.getId(), item.getId());
        assertEquals(itemOne.getName(), item.getName());
        assertEquals(itemOne.getDescription(), item.getDescription());
        assertEquals(itemOne.getAvailable(), item.getAvailable());
        assertEquals(lastBooking.getId(), item.getLastBooking().getId());
        assertEquals(lastBookingAuthor.getId(), item.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), item.getNextBooking().getId());
        assertEquals(nextBookingAuthor.getId(), item.getNextBooking().getBookerId());

        var itemsComments = item.getComments().toArray(new CommentDto[1]);

        assertEquals(comment.getId(), itemsComments[0].getId());
        assertEquals(comment.getText(), itemsComments[0].getText());
        assertEquals(comment.getUser().getName(), itemsComments[0].getAuthorName());
        assertEquals(comment.getCreationDate(), itemsComments[0].getCreated());
        assertNull(item.getRequestId());
    }

    @Test
    void createComment() {
        var userOne = userRepository.save(createUser());
        var userTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(userOne, "Item 1", "description 1"));
        var lastBookingAuthor = userRepository.save(createUser());
        var inputCommentDto = createCommentDto();

        bookingRepository.save(createBooking(lastBookingAuthor, itemOne,
                LocalDateTime.now().minusDays(4)));

        itemRepository.save(createItem(userTwo, "Item 2", "description 2"));

        var outputCommentDto = assertDoesNotThrow(() -> itemService.createComment(lastBookingAuthor.getId(), itemOne.getId(),
                inputCommentDto));

        assertEquals(inputCommentDto.getText(), outputCommentDto.getText());
        assertEquals(lastBookingAuthor.getName(), outputCommentDto.getAuthorName());
        assertNotNull(outputCommentDto.getCreated());
    }

    private long getNextUserId() {
        return nextUserId++;
    }

    private long getNextItemId() {
        return nextItemId++;
    }

    private Item createItem(User owner, String name, String description) {
        var item = new Item();

        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextUserId();

        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Booking createBooking(User author, Item item, LocalDateTime start) {
        var booking = new Booking();

        booking.setItem(item);
        booking.setUser(author);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(start);
        booking.setEndTime(start.plusDays(1));
        return booking;
    }

    private ItemDto createItemDto() {
        var itemDto = new ItemDto();
        var itemId = getNextItemId();

        itemDto.setName("Item " + itemId);
        itemDto.setDescription("Item description " + itemId);
        itemDto.setAvailable(true);
        return itemDto;
    }

    private Comment createComment(User user, Item item) {
        var comment = new Comment();

        comment.setUser(user);
        comment.setItem(item);
        comment.setText("Comment");
        return comment;
    }

    private CommentDto createCommentDto() {
        var commentDto = new CommentDto();

        commentDto.setText("Text of comment");
        return commentDto;
    }
}