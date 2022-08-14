package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.BookingToCreateCommentNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Item> getAllItemsForUser(long userId) {
        var user = userService.getUserById(userId);
        var items = user.getItems(); //itemDao.findAllByOwnerIdOrderById(userId);
        var refTime = LocalDateTime.now();

        for (Item currentItem : items) {
            addBookingInfo(currentItem, refTime);
            currentItem.getComments();
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Item getItemById(long itemId) {
        return itemDao.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Элемент с id = %d не найден", itemId)));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Item createNewItem(long ownerId, Item item) {
        var user = userService.getUserById(ownerId);

        item.setOwner(user);
        return itemDao.save(item);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Item updateItem(long ownerId, Item item) {
        var user = userService.getUserById(ownerId);

        var updatedItem = getItemById(item.getId());

        if (!updatedItem.getOwner().equals(user)) {
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %d не владелец элемента с id = %d",
                    ownerId, updatedItem.getId()));
        }

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Item> findItemsByNameAndDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.findByNameOrDescriptionLikeAndIsAvailableTrue(text);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Item getItemById(long userId, long itemId) {
        var user = userService.getUserById(userId);
        var item = getItemById(itemId);

        if (item.getOwner().equals(user)) {
            addBookingInfo(item, LocalDateTime.now());
        }
        item.getComments();
        return item;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Comment createComment(long userId, long itemId, String text) {
        var creatingTime = LocalDateTime.now();
        var wrappedBooking = bookingRepository.findByItemIdAndUserIdAndEndTimeBefore(itemId, userId,
                creatingTime);

        if (wrappedBooking.isEmpty()) {
            throw new BookingToCreateCommentNotFoundException(String.format("Бронирование с параметрами userId = %d, " +
                    "itemId = %d, endTime < %3$tFT%3%tT", userId, itemId, creatingTime));
        }

        var comment = new Comment(null, wrappedBooking.get().getUser(), wrappedBooking.get().getItem(), creatingTime,
                text);

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    private Item addBookingInfo(@NonNull Item item, @NonNull LocalDateTime referenceTime) {
        Collection<Booking> bookings = bookingRepository.findAllByItem(item);
        var bookingInfo = getLastNextBooking(bookings, referenceTime);

        item.setLastBooking(bookingInfo.getLastBooking());
        item.setNextBooking(bookingInfo.getNextBooking());

        return item;
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
}
