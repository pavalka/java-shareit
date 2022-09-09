package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingRepositoryTest {
    private static long nextNum = 1;

    private final UserDao userRepository;
    private final ItemDao itemRepository;
    private final BookingRepository bookingRepository;

    @Test
    void findAllByUserReturnBookingOne() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var item = itemRepository.save(createItem(itemOwner));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, item, LocalDateTime.now()));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorTwo, item, LocalDateTime.now().plusDays(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, item, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUser(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserReturnBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var item = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(1, 1, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorOne, item, LocalDateTime.now()));
        bookingRepository.save(createBooking(bookingAuthorTwo, item, LocalDateTime.now().plusDays(2)));

        var bookingThree = bookingRepository.save(createBooking(bookingAuthorOne, item,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUser(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingThree.getId(), result.get(0).getId());
        assertEquals(bookingThree.getItem(), result.get(0).getItem());
        assertEquals(bookingThree.getUser(), result.get(0).getUser());
        assertEquals(bookingThree.getStatus(), result.get(0).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserReturnBookingOneAndBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var item = itemRepository.save(createItem(itemOwner));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, item, LocalDateTime.now()));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorTwo, item, LocalDateTime.now().plusDays(2)));

        var bookingThree = bookingRepository.save(createBooking(bookingAuthorOne, item,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUser(bookingAuthorOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingThree.getId(), result.get(1).getId());
        assertEquals(bookingThree.getItem(), result.get(1).getItem());
        assertEquals(bookingThree.getUser(), result.get(1).getUser());
        assertEquals(bookingThree.getStatus(), result.get(1).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsCurrentReturnBookingOne() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsCurrent(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsCurrentReturnBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(1, 1, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusHours(2)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsCurrent(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsCurrentReturnBookingOneAndBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusHours(2)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsCurrent(bookingAuthorOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsPastReturnBookingOneAndBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusDays(20)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusDays(25)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsPast(bookingAuthorOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(1).getId());
        assertEquals(bookingTwo.getItem(), result.get(1).getItem());
        assertEquals(bookingTwo.getUser(), result.get(1).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(1).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsPastReturnBookingOne() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusDays(20)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusDays(25)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsPast(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsPastReturnBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(1, 1, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(20)));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusDays(25)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsPast(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsFutureReturnBookingOneAndBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsFuture(bookingAuthorOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsFutureReturnBookingOne() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsFuture(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStateIsFutureReturnBookingTwo() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(1, 3, Sort.unsorted());

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2)));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByUserAndStateIsFuture(bookingAuthorOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStatusReturnBookingWithStatusApproved() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingOne = bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingThree = bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByUserAndStatus(bookingAuthorOne, BookingStatus.APPROVED,
                pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingThree.getId(), result.get(1).getId());
        assertEquals(bookingThree.getItem(), result.get(1).getItem());
        assertEquals(bookingThree.getUser(), result.get(1).getUser());
        assertEquals(bookingThree.getStatus(), result.get(1).getStatus());
        assertEquals(BookingStatus.APPROVED, result.get(1).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByUserAndStatusReturnBookingWithStatusRejected() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingTwo = bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByUserAndStatus(bookingAuthorOne, BookingStatus.REJECTED,
                pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByUserAndStatusReturnBookingWithStatusWaiting() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var itemTwo = itemRepository.save(createItem(itemOwner));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));

        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo,
                LocalDateTime.now().minusHours(2)));
        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByUserAndStatus(bookingAuthorOne, BookingStatus.WAITING,
                pageable);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getItem(), result.get(0).getItem());
        assertEquals(booking.getUser(), result.get(0).getUser());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
        assertEquals(booking.getStartTime(), result.get(0).getStartTime());
        assertEquals(booking.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemOwnerReturnThreeBookings() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 5, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));
        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().plusDays(21)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingThree = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByItemOwner(itemOwnerOne, pageable);

        assertEquals(3, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingThree.getId(), result.get(1).getId());
        assertEquals(bookingThree.getItem(), result.get(1).getItem());
        assertEquals(bookingThree.getUser(), result.get(1).getUser());
        assertEquals(bookingThree.getStatus(), result.get(1).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingOne.getId(), result.get(2).getId());
        assertEquals(bookingOne.getItem(), result.get(2).getItem());
        assertEquals(bookingOne.getUser(), result.get(2).getUser());
        assertEquals(bookingOne.getStatus(), result.get(2).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(2).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(2).getEndTime());
    }

    @Test
    void findAllByItemOwnerReturnOneBookingFromIs0SizeIs1() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.by(Sort.Direction.DESC, "startTime"));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().plusDays(21)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByItemOwner(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemOwnerReturnTwoBookingsFromIs1SizeIs2() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingThree = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByItemOwner(itemOwnerOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingThree.getId(), result.get(0).getId());
        assertEquals(bookingThree.getItem(), result.get(0).getItem());
        assertEquals(bookingThree.getUser(), result.get(0).getUser());
        assertEquals(bookingThree.getStatus(), result.get(0).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsCurrentReturnTwoBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));
        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().minusHours(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsCurrent(itemOwnerOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsCurrentReturnOneBookingFromIs0SizeIs1() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.by(Sort.Direction.DESC, "startTime"));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusHours(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().minusHours(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsCurrent(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsCurrentReturnOneBookingsFromIs1SizeIs2() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusHours(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().minusHours(1)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsCurrent(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsPastReturnTwoBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusDays(2)));
        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().minusDays(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsPast(itemOwnerOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsPastReturnOneBookingsFromIs0SizeIs1() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.by(Sort.Direction.DESC, "startTime"));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne,
                LocalDateTime.now().minusDays(1)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsPast(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsPastReturnOneBookingFromIs1SizeIs2() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().minusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().minusDays(1)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsPast(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsFutureReturnTwoBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().minusDays(1)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsFuture(itemOwnerOne, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(1).getId());
        assertEquals(bookingOne.getItem(), result.get(1).getItem());
        assertEquals(bookingOne.getUser(), result.get(1).getUser());
        assertEquals(bookingOne.getStatus(), result.get(1).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(1).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByOwnerAndStateIsFutureReturnOneBookingsFromIs1SizeIs2() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().minusDays(1)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findAllByOwnerAndStateIsFuture(itemOwnerOne, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemOwnerAndStatusReturnApprovedBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingOne = bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingThree = bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByItemOwnerAndStatus(itemOwnerOne, BookingStatus.APPROVED,
                pageable);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingThree.getId(), result.get(1).getId());
        assertEquals(bookingThree.getItem(), result.get(1).getItem());
        assertEquals(bookingThree.getUser(), result.get(1).getUser());
        assertEquals(bookingThree.getStatus(), result.get(1).getStatus());
        assertEquals(BookingStatus.APPROVED, result.get(1).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByItemOwnerAndStatusReturnApprovedBookingsFromIs1SizeIs2() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingThree = bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByItemOwnerAndStatus(itemOwnerOne, BookingStatus.APPROVED,
                pageable);

        assertEquals(1, result.size());
        assertEquals(bookingThree.getId(), result.get(0).getId());
        assertEquals(bookingThree.getItem(), result.get(0).getItem());
        assertEquals(bookingThree.getUser(), result.get(0).getUser());
        assertEquals(bookingThree.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
        assertEquals(bookingThree.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingThree.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemOwnerAndStatusReturnRejectedBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingOne);
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingTwo = bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByItemOwnerAndStatus(itemOwnerOne, BookingStatus.REJECTED,
                pageable);

        assertEquals(1, result.size());
        assertEquals(bookingTwo.getId(), result.get(0).getId());
        assertEquals(bookingTwo.getItem(), result.get(0).getItem());
        assertEquals(bookingTwo.getUser(), result.get(0).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemOwnerAndStatusReturnWaitingBookingsFromIs0SizeIs3() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var pageable = new PageableByOffsetAndSize(0, 3, Sort.by(Sort.Direction.DESC, "startTime"));
        var bookingOne = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2));

        bookingOne.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingOne);

        var booking = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(21)));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now().minusHours(2)));

        var bookingTwo = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(5));

        bookingTwo.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingTwo);

        var bookingThree = createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().minusDays(5));

        bookingThree.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingThree);

        var result = bookingRepository.findAllByItemOwnerAndStatus(itemOwnerOne, BookingStatus.WAITING,
                pageable);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getItem(), result.get(0).getItem());
        assertEquals(booking.getUser(), result.get(0).getUser());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
        assertEquals(booking.getStartTime(), result.get(0).getStartTime());
        assertEquals(booking.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findByIdAndUserOrOwnerReturnBookingFoundByBookingAuthor() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne,
                LocalDateTime.now().plusDays(2)));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findByIdAndUserOrOwner(booking.getId(), bookingAuthorOne);

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem(), result.get().getItem());
        assertEquals(booking.getUser(), result.get().getUser());
        assertEquals(booking.getStatus(), result.get().getStatus());
        assertEquals(booking.getStartTime(), result.get().getStartTime());
        assertEquals(booking.getEndTime(), result.get().getEndTime());
    }

    @Test
    void findByIdAndUserOrOwnerReturnBookingFoundByItemOwner() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2)));

        var booking = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findByIdAndUserOrOwner(booking.getId(), itemOwner);

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem(), result.get().getItem());
        assertEquals(booking.getUser(), result.get().getUser());
        assertEquals(booking.getStatus(), result.get().getStatus());
        assertEquals(booking.getStartTime(), result.get().getStartTime());
        assertEquals(booking.getEndTime(), result.get().getEndTime());
    }

    @Test
    void findByIdAndUserOrOwnerReturnNoBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwner = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwner));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now().plusDays(2)));

        var booking = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now().plusDays(5)));

        var result = bookingRepository.findByIdAndUserOrOwner(booking.getId(), bookingAuthorOne);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemAndTimeConflictsReturnEmptyListWhenCheckAnotherItem() {
        var bookingAuthorOne = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var creatingTime = LocalDateTime.now();

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        var result = bookingRepository.findAllByItemAndTimeConflicts(itemTwo, creatingTime.plusHours(1),
                creatingTime.plusDays(1));

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemAndTimeConflictsReturnBookingWhenStartTimeIsConflicting() {
        var bookingAuthorOne = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var creatingTime = LocalDateTime.now();
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        var result = bookingRepository.findAllByItemAndTimeConflicts(itemOne, creatingTime.plusHours(1),
                creatingTime.plusDays(2));

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getItem(), result.get(0).getItem());
        assertEquals(booking.getUser(), result.get(0).getUser());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
        assertEquals(booking.getStartTime(), result.get(0).getStartTime());
        assertEquals(booking.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemAndTimeConflictsReturnBookingWhenEndTimeIsConflicting() {
        var bookingAuthorOne = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var creatingTime = LocalDateTime.now();
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        var result = bookingRepository.findAllByItemAndTimeConflicts(itemOne, creatingTime.minusHours(16),
                creatingTime.plusHours(7));

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getItem(), result.get(0).getItem());
        assertEquals(booking.getUser(), result.get(0).getUser());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
        assertEquals(booking.getStartTime(), result.get(0).getStartTime());
        assertEquals(booking.getEndTime(), result.get(0).getEndTime());
    }

    @Test
    void findAllByItemAndTimeConflictsReturnNoBookingWhenNoTimeConflict() {
        var bookingAuthorOne = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var creatingTime = LocalDateTime.now();

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, creatingTime));

        var result = bookingRepository.findAllByItemAndTimeConflicts(itemOne, creatingTime.minusDays(1),
                creatingTime.minusHours(7));

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndItemOwnerReturnBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now()));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now()));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now()));

        var result = bookingRepository.findByIdAndItemOwner(booking.getId(), itemOwnerOne);

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem(), result.get().getItem());
        assertEquals(booking.getUser(), result.get().getUser());
        assertEquals(booking.getStatus(), result.get().getStatus());
        assertEquals(booking.getStartTime(), result.get().getStartTime());
        assertEquals(booking.getEndTime(), result.get().getEndTime());
    }

    @Test
    void findByIdAndItemOwnerReturnNoBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now()));

        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now()));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now()));

        var result = bookingRepository.findByIdAndItemOwner(booking.getId(), itemOwnerTwo);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemReturnTwoBookings() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var bookingOne = bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now()));
        var bookingTwo = bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now()));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now()));

        var result = bookingRepository.findAllByItem(itemOne);

        assertEquals(2, result.size());
        assertEquals(bookingOne.getId(), result.get(0).getId());
        assertEquals(bookingOne.getItem(), result.get(0).getItem());
        assertEquals(bookingOne.getUser(), result.get(0).getUser());
        assertEquals(bookingOne.getStatus(), result.get(0).getStatus());
        assertEquals(bookingOne.getStartTime(), result.get(0).getStartTime());
        assertEquals(bookingOne.getEndTime(), result.get(0).getEndTime());
        assertEquals(bookingTwo.getId(), result.get(1).getId());
        assertEquals(bookingTwo.getItem(), result.get(1).getItem());
        assertEquals(bookingTwo.getUser(), result.get(1).getUser());
        assertEquals(bookingTwo.getStatus(), result.get(1).getStatus());
        assertEquals(bookingTwo.getStartTime(), result.get(1).getStartTime());
        assertEquals(bookingTwo.getEndTime(), result.get(1).getEndTime());
    }

    @Test
    void findAllByItemReturnNoBookings() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var itemThree = itemRepository.save(createItem(itemOwnerTwo));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, LocalDateTime.now()));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, LocalDateTime.now()));
        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, LocalDateTime.now()));

        var result = bookingRepository.findAllByItem(itemThree);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemAndUserAndEndTimeBeforeReturnBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var startTime = LocalDateTime.now();

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, startTime.minusDays(5)));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, startTime.minusDays(7)));

        var booking = bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, startTime.minusDays(4)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, startTime));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemTwo, startTime.minusHours(23)));

        var result = bookingRepository.findByItemAndUserAndEndTimeBefore(itemTwo, bookingAuthorOne, startTime);

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
        assertEquals(booking.getItem(), result.get().getItem());
        assertEquals(booking.getUser(), result.get().getUser());
        assertEquals(booking.getStatus(), result.get().getStatus());
        assertEquals(booking.getStartTime(), result.get().getStartTime());
        assertEquals(booking.getEndTime(), result.get().getEndTime());
    }

    @Test
    void findByItemAndUserAndEndTimeBeforeReturnNoBooking() {
        var bookingAuthorOne = userRepository.save(createUser());
        var bookingAuthorTwo = userRepository.save(createUser());
        var itemOwnerOne = userRepository.save(createUser());
        var itemOwnerTwo = userRepository.save(createUser());
        var itemOne = itemRepository.save(createItem(itemOwnerOne));
        var itemTwo = itemRepository.save(createItem(itemOwnerTwo));
        var startTime = LocalDateTime.now();

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, startTime.minusDays(5)));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemOne, startTime.minusDays(7)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemTwo, startTime.minusDays(4)));

        bookingRepository.save(createBooking(bookingAuthorOne, itemOne, startTime));
        bookingRepository.save(createBooking(bookingAuthorTwo, itemTwo, startTime.minusHours(23)));

        var result = bookingRepository.findByItemAndUserAndEndTimeBefore(itemTwo, bookingAuthorTwo, startTime);

        assertTrue(result.isEmpty());
    }

    private long getNextNum() {
        return nextNum++;
    }

    private Item createItem(User owner) {
        var item = new Item();
        var itemId = getNextNum();

        item.setName("Item " + itemId);
        item.setDescription("Item description " + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextNum();

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
}