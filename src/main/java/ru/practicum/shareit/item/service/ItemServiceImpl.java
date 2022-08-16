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
import ru.practicum.shareit.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<Item> getAllItemsForUser(@NonNull User user) {
        var items = user.getItems();
        var refTime = LocalDateTime.now();

        for (Item currentItem : items) {
            addBookingInfo(currentItem, refTime);
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
    public Item createNewItem(Item item) {
        return itemDao.save(item);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Item updateItem(Item item, User user) {
        var updatedItem = getItemById(item.getId());

        if (!updatedItem.getOwner().equals(user)) {
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %d не владелец элемента с id = %d",
                    user.getId(), updatedItem.getId()));
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
    public Item getItemByIdAndUser(User user, long itemId) {
        var item = getItemById(itemId);

        if (item.getOwner().equals(user)) {
            addBookingInfo(item, LocalDateTime.now());
        }
        return item;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Comment createComment(Comment comment) {
        var wrappedBooking = bookingRepository.findByItemAndUserAndEndTimeBefore(comment.getItem(),
                comment.getUser(), comment.getCreationDate());

        if (wrappedBooking.isEmpty()) {
            throw new BookingToCreateCommentNotFoundException(String.format("Бронирование с параметрами userId = %d, " +
                    "itemId = %d, endTime < %3$tFT%3$tT не найдено", comment.getUser().getId(), comment.getItem()
                            .getId(), comment.getCreationDate()));
        }
        return commentRepository.save(comment);
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
}
