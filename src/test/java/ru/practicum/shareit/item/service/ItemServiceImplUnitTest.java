package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BookingToCreateCommentNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.exceptions.RequestNotFoundException;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    private long nextUserId = 1;
    private long nextItemId = 1;
    private long nextBookingId = 1;
    private long nextCommentId = 1;
    private long nextRequestId = 1;

    @Mock
    private ItemDao itemRepository;

    @Mock
    private UserDao userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getAllItemsForUserThrowsExceptionWhenUserIdIsIllegal() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> itemService.getAllItemsForUser(10, 0, 1));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void getAllItemsForUserReturnItemDto() {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var lastBookingAuthor = createUser();
        var nextBookingAuthor = createUser();
        var lastBooking = createBooking(lastBookingAuthor, item, LocalDateTime.now().minusDays(5));
        var nextBooking = createBooking(nextBookingAuthor, item, LocalDateTime.now().plusDays(5));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(itemRepository.findAllByOwner(itemOwner, pageable))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findAllByItem(item))
                .thenReturn(List.of(lastBooking, nextBooking));

        var itemDto = assertDoesNotThrow(() -> itemService.getAllItemsForUser(itemOwner.getId(), 0, 1))
                .toArray(new ItemDto[1]);

        assertEquals(1, itemDto.length);
        assertEquals(item.getId(), itemDto[0].getId());
        assertEquals(item.getName(), itemDto[0].getName());
        assertEquals(item.getDescription(), itemDto[0].getDescription());
        assertEquals(item.getAvailable(), itemDto[0].getAvailable());
        assertEquals(lastBooking.getId(), itemDto[0].getLastBooking().getId());
        assertEquals(lastBooking.getUser().getId(), itemDto[0].getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), itemDto[0].getNextBooking().getId());
        assertEquals(nextBooking.getUser().getId(), itemDto[0].getNextBooking().getBookerId());
        assertNull(itemDto[0].getRequestId());
        assertNull(itemDto[0].getComments());
    }

    @Test
    void createNewItemThrowsExceptionWhenUserIdIsIllegal() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> itemService.createNewItem(10, createItemDto()));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void createNewItemReturnItemDtoWhenRequestIdIsNull() {
        var itemOwner = createUser();
        var inputItemDto = createItemDto();

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    var item = invocation.<Item>getArgument(0);
                    item.setId(getNextItemId());
                    return item;
                });

        var outputItemDto = assertDoesNotThrow(() -> itemService.createNewItem(itemOwner.getId(), inputItemDto));

        assertEquals(inputItemDto.getName(), outputItemDto.getName());
        assertEquals(inputItemDto.getDescription(), outputItemDto.getDescription());
        assertEquals(inputItemDto.getAvailable(), outputItemDto.getAvailable());
        assertNull(outputItemDto.getRequestId());
        assertNull(outputItemDto.getComments());
        assertNull(outputItemDto.getLastBooking());
        assertNull(outputItemDto.getNextBooking());

        Mockito.verify(requestRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void createNewItemReturnItemDtoWhenRequestIdIsNotNull() {
        var itemOwner = createUser();
        var inputItemDto = createItemDto();
        var requestAuthor = createUser();
        var request = createRequest(requestAuthor);

        inputItemDto.setRequestId(request.getId());

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    var item = invocation.<Item>getArgument(0);
                    item.setId(getNextItemId());
                    return item;
                });

        Mockito.when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        var outputItemDto = assertDoesNotThrow(() -> itemService.createNewItem(itemOwner.getId(), inputItemDto));

        assertEquals(inputItemDto.getName(), outputItemDto.getName());
        assertEquals(inputItemDto.getDescription(), outputItemDto.getDescription());
        assertEquals(inputItemDto.getAvailable(), outputItemDto.getAvailable());
        assertEquals(request.getId(), outputItemDto.getRequestId());
        assertNull(outputItemDto.getComments());
        assertNull(outputItemDto.getLastBooking());
        assertNull(outputItemDto.getNextBooking());

        Mockito.verify(requestRepository, Mockito.times(1)).findById(request.getId());
    }

    @Test
    void createNewItemThrowsExceptionWhenRequestIdIsIllegal() {
        var itemOwner = createUser();
        var inputItemDto = createItemDto();

        inputItemDto.setRequestId(10L);

        Mockito.when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));

        Mockito.when(requestRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(RequestNotFoundException.class,
                () -> itemService.createNewItem(itemOwner.getId(), inputItemDto));

        assertEquals("Запрос с id = 10 не найден", ex.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void updateItemThrowsExceptionWhenItemIdIsIllegal() {
        var itemDto = createItemDto();

        Mockito.when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.empty());

        var ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, 10000));

        assertEquals(String.format("Элемент с id = %d не найден", itemDto.getId()), ex.getMessage());
    }

    @Test
    void updateItemThrowsExceptionWhenUserIsNotItemOwner() {
        var itemDto = createItemDto();
        var itemOwner = createUser();
        var item = ItemMapper.mapItemDtoToItem(itemDto, itemOwner);

        item.setId(itemDto.getId());

        Mockito.when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(item));

        var ex = assertThrows(UserIsNotItemOwnerException.class,
                () -> itemService.updateItem(itemDto, itemOwner.getId() + 100));

        var errMsg = String.format("Пользователь с id = %d не владелец элемента с id = %d",itemOwner.getId() + 100,
                item.getId());

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void updateItemUpdateNameWhenNameIsNotNull() {
        var itemDto = createItemDto();
        var itemOwner = createUser();
        var item = ItemMapper.mapItemDtoToItem(itemDto, itemOwner);

        item.setId(itemDto.getId());

        Mockito.when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(item));

        var newItemDto = new ItemDto();

        newItemDto.setId(itemDto.getId());
        newItemDto.setName("New name");

        var resultingItemDto = assertDoesNotThrow(() -> itemService.updateItem(newItemDto, itemOwner.getId()));

        assertEquals(itemDto.getId(), resultingItemDto.getId());
        assertEquals(newItemDto.getName(), resultingItemDto.getName());
        assertEquals(itemDto.getDescription(), resultingItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultingItemDto.getAvailable());
        assertNull(resultingItemDto.getRequestId());
        assertNull(resultingItemDto.getComments());
        assertNull(resultingItemDto.getLastBooking());
        assertNull(resultingItemDto.getNextBooking());
    }

    @Test
    void updateItemUpdateDescriptionWhenDescriptionIsNotNull() {
        var itemDto = createItemDto();
        var itemOwner = createUser();
        var item = ItemMapper.mapItemDtoToItem(itemDto, itemOwner);

        item.setId(itemDto.getId());

        Mockito.when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(item));

        var newItemDto = new ItemDto();

        newItemDto.setId(itemDto.getId());
        newItemDto.setDescription("New description");

        var resultingItemDto = assertDoesNotThrow(() -> itemService.updateItem(newItemDto, itemOwner.getId()));

        assertEquals(itemDto.getId(), resultingItemDto.getId());
        assertEquals(itemDto.getName(), resultingItemDto.getName());
        assertEquals(newItemDto.getDescription(), resultingItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultingItemDto.getAvailable());
        assertNull(resultingItemDto.getRequestId());
        assertNull(resultingItemDto.getComments());
        assertNull(resultingItemDto.getLastBooking());
        assertNull(resultingItemDto.getNextBooking());
    }

    @Test
    void updateItemUpdateAvailableWhenAvailableIsNotNull() {
        var itemDto = createItemDto();
        var itemOwner = createUser();
        var item = ItemMapper.mapItemDtoToItem(itemDto, itemOwner);

        item.setId(itemDto.getId());

        Mockito.when(itemRepository.findById(itemDto.getId()))
                .thenReturn(Optional.of(item));

        var newItemDto = new ItemDto();

        newItemDto.setId(itemDto.getId());
        newItemDto.setAvailable(false);

        assertTrue(itemDto.getAvailable());

        var resultingItemDto = assertDoesNotThrow(() -> itemService.updateItem(newItemDto, itemOwner.getId()));

        assertEquals(itemDto.getId(), resultingItemDto.getId());
        assertEquals(itemDto.getName(), resultingItemDto.getName());
        assertEquals(item.getDescription(), resultingItemDto.getDescription());
        assertEquals(newItemDto.getAvailable(), resultingItemDto.getAvailable());
        assertNull(resultingItemDto.getRequestId());
        assertNull(resultingItemDto.getComments());
        assertNull(resultingItemDto.getLastBooking());
        assertNull(resultingItemDto.getNextBooking());
    }

    @Test
    void findItemsByNameAndDescriptionReturnEmptyCollectionWhenTextIsBlank() {
        var result = assertDoesNotThrow(() -> itemService.findItemsByNameAndDescription("  ", 0, 1));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsByNameAndDescriptionReturnItems() {
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());
        var itemOwner = createUser();
        var item = createItem(itemOwner);

        Mockito.when(itemRepository.findByNameOrDescriptionLikeAndIsAvailableTrue(Mockito.eq("Item"),
                        Mockito.eq(pageable)))
                .thenReturn(List.of(item));

        var result = assertDoesNotThrow(() -> itemService.findItemsByNameAndDescription("Item", 0, 1))
                .toArray(new ItemDto[1]);

        assertEquals(1, result.length);
        assertEquals(item.getId(),result[0].getId());
        assertEquals(item.getName(),result[0].getName());
        assertEquals(item.getDescription(),result[0].getDescription());
        assertEquals(item.getAvailable(),result[0].getAvailable());
    }

    @Test
    void getItemByIdAndUserThrowsExceptionWhenItemIdIsIllegal() {
        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemByIdAndUser(1, 10));

        assertEquals("Элемент с id = 10 не найден", ex.getMessage());
    }

    @Test
    void getItemByIdAndUserReturnItemDtoWhenUserIsItemOwner() {
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var lastBookingAuthor = createUser();
        var nextBookingAuthor = createUser();
        var lastBooking = createBooking(lastBookingAuthor, item, LocalDateTime.now().minusDays(5));
        var nextBooking = createBooking(nextBookingAuthor, item, LocalDateTime.now().plusDays(5));

        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findAllByItem(item))
                .thenReturn(List.of(lastBooking, nextBooking));

        var itemDto = assertDoesNotThrow(() -> itemService.getItemByIdAndUser(itemOwner.getId(), item.getId()));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(lastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(lastBooking.getUser().getId(), itemDto.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), itemDto.getNextBooking().getId());
        assertEquals(nextBooking.getUser().getId(), itemDto.getNextBooking().getBookerId());
        assertNull(itemDto.getRequestId());
        assertNull(itemDto.getComments());

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItem(Mockito.any());
    }

    @Test
    void getItemByIdAndUserReturnItemDtoWhenUserIsNotItemOwner() {
        var itemOwner = createUser();
        var item = createItem(itemOwner);

        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        var itemDto = assertDoesNotThrow(() -> itemService.getItemByIdAndUser(itemOwner.getId() + 100,
                item.getId()));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
        assertNull(itemDto.getComments());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());

        Mockito.verify(bookingRepository, Mockito.never()).findAllByItem(Mockito.any());
    }

    @Test
    void createCommentThrowsExceptionWhenUserIdIsIllegal() {
        var errMsg = "Пользователь с id = 10 не найден";
        var commentAuthor = createUser();

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class,
                () -> itemService.createComment(10, 23, createCommentDto(commentAuthor, "Comment")));

        assertEquals(errMsg, ex.getMessage());

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCommentThrowsExceptionWhenItemIdIsIllegal() {
        var errMsg = "Элемент с id = 10 не найден";
        var commentAuthor = createUser();

        Mockito.when(userRepository.findById(commentAuthor.getId()))
                .thenReturn(Optional.of(commentAuthor));

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.createComment(commentAuthor.getId(), 10, createCommentDto(commentAuthor, "Comment")));

        assertEquals(errMsg, ex.getMessage());

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCommentThrowsExceptionWhenNoBookingForItem() {
        var commentAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);

        Mockito.when(userRepository.findById(commentAuthor.getId()))
                .thenReturn(Optional.of(commentAuthor));

        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findByItemAndUserAndEndTimeBefore(Mockito.eq(item), Mockito.eq(commentAuthor),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(BookingToCreateCommentNotFoundException.class,
                () -> itemService.createComment(commentAuthor.getId(), item.getId(),
                        createCommentDto(commentAuthor, "Comment")));

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCommentCreateComment() {
        var commentAuthor = createUser();
        var itemOwner = createUser();
        var item = createItem(itemOwner);
        var booking = createBooking(commentAuthor, item, LocalDateTime.now().minusDays(2));
        var inputCommentDto = createCommentDto(commentAuthor, "Comment one");

        Mockito.when(userRepository.findById(commentAuthor.getId()))
                .thenReturn(Optional.of(commentAuthor));

        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findByItemAndUserAndEndTimeBefore(Mockito.eq(item), Mockito.eq(commentAuthor),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> {
                    var comment = invocation.<Comment>getArgument(0);

                    comment.setId(getNextCommentId());
                    return comment;
                });

        var outputCommentDto = assertDoesNotThrow(() -> itemService.createComment(commentAuthor.getId(), item.getId(),
                inputCommentDto));

        assertEquals(inputCommentDto.getText(), outputCommentDto.getText());
        assertEquals(commentAuthor.getName(), outputCommentDto.getAuthorName());
        assertNotNull(outputCommentDto.getCreated());
        assertNotEquals(0, outputCommentDto.getId());
    }

    private long getNextUserId() {
        return nextUserId++;
    }

    private long getNextItemId() {
        return nextItemId++;
    }

    private long getNextBookingId() {
        return nextBookingId++;
    }

    private long getNextCommentId() {
        return nextCommentId++;
    }

    private long getNextRequestId() {
        return nextRequestId++;
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

    private CommentDto createCommentDto(User user, String text) {
        var commentDto = new CommentDto();

        commentDto.setId(getNextCommentId());
        commentDto.setAuthorName(user.getName());
        commentDto.setText(text);
        return commentDto;
    }
}