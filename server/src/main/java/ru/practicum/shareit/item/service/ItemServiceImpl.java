package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserDao userDao;
    private final ItemRequestRepository requestRepository;

    @Override
    public Collection<ItemDto> getAllItemsForUser(long userId, long from, int size) {
        var user = getUserById(userId);
        var pageable = new PageableByOffsetAndSize(from, size, Sort.by(Sort.Direction.ASC, "id"));
        var items = itemDao.findAllByOwner(user, pageable);
        var refTime = LocalDateTime.now();

        for (Item currentItem : items) {
            addBookingInfo(currentItem, refTime);
        }

        return ItemMapper.mapItemsCollectionToItemDto(items);
    }

    @Override
    @Transactional
    public ItemDto createNewItem(long ownerId, IncomingItemDto itemDto) {
        var owner = getUserById(ownerId);
        Item item;

        if (itemDto.getRequestId() == null) {
            item  = ItemMapper.mapItemDtoToItem(itemDto, owner);
        } else {
            var request = getRequestById(itemDto.getRequestId());

            item = ItemMapper.mapItemDtoToItem(itemDto, owner, request);
        }
        return ItemMapper.mapItemToItemDto(itemDao.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(IncomingItemDto itemDto, long userId) {
        var updatedItem = getItemById(itemDto.getId());

        if (updatedItem.getOwner().getId() != userId) {
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %d не владелец элемента с id = %d",
                    userId, updatedItem.getId()));
        }

        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapItemToItemDto(updatedItem);
    }

    @Override
    public Collection<ItemDto> findItemsByNameAndDescription(String text, long from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapItemsCollectionToItemDto(itemDao
                .findByNameOrDescriptionLikeAndIsAvailableTrue(text,
                        new PageableByOffsetAndSize(from, size, Sort.unsorted())
                ));
    }

    @Override
    public ItemDto getItemByIdAndUser(long userId, long itemId) {
        var item = getItemById(itemId);

        if (item.getOwner().getId() == userId) {
            addBookingInfo(item, LocalDateTime.now());
        }
        return ItemMapper.mapItemToItemDto(item);
    }

    @Override
    @Transactional
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        var user = getUserById(userId);
        var item = getItemById(itemId);
        var comment = CommentMapper.mapDtoToComment(commentDto, item, user);
        var wrappedBooking = bookingRepository.findByItemAndUserAndEndTimeBefore(item, user,
                comment.getCreationDate());

        if (wrappedBooking.isEmpty()) {
            throw new BookingToCreateCommentNotFoundException(String.format("Бронирование с параметрами userId = %d, " +
                    "itemId = %d, endTime < %3$tFT%3$tT не найдено", user.getId(), item.getId(),
                    comment.getCreationDate()));
        }
        return CommentMapper.mapCommentToDto(commentRepository.save(comment));
    }

    private void addBookingInfo(@NonNull Item item, @NonNull LocalDateTime referenceTime) {
        Collection<Booking> bookings = bookingRepository.findAllByItem(item);
        var bookingInfo = getLastNextBooking(bookings, referenceTime);

        item.setLastBooking(bookingInfo.getLastBooking());
        item.setNextBooking(bookingInfo.getNextBooking());
    }

    private BookingInfo getLastNextBooking(@NonNull Collection<Booking> bookings,
                                           @NonNull LocalDateTime referenceTime) {
        var bookingInfo = new BookingInfo();
        Duration nextBookingMinDiff = Duration.between(referenceTime, LocalDateTime.MAX);
        Duration lastBookingMinDiff = Duration.between(LocalDateTime.MIN, referenceTime);

        for (Booking currentBooking : bookings) {
            Duration diff;

            if (currentBooking.getEndTime().isBefore(referenceTime)) {
                diff = Duration.between(currentBooking.getEndTime(), referenceTime);
                if (diff.compareTo(lastBookingMinDiff) < 0) {
                    lastBookingMinDiff = diff;
                    bookingInfo.setLastBooking(currentBooking);
                }
            } else if (currentBooking.getStartTime().isAfter(referenceTime)) {
                diff = Duration.between(referenceTime, currentBooking.getStartTime());
                if (diff.compareTo(nextBookingMinDiff) < 0) {
                    nextBookingMinDiff = diff;
                    bookingInfo.setNextBooking(currentBooking);
                }
            }
        }
        return bookingInfo;
    }

    private User getUserById(long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    private Item getItemById(long itemId) {
        return itemDao.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Элемент с id = %d не найден", itemId)));
    }

    private ItemRequest getRequestById(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException(String.format("Запрос с id = %d не найден", requestId))
        );
    }
}
