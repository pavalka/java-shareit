package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUser(User user, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND CURRENT_TIMESTAMP BETWEEN b.startTime AND b.endTime")
    List<Booking> findAllByUserAndStateIsCurrent(@Param("user") User user, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.endTime < CURRENT_TIMESTAMP")
    List<Booking> findAllByUserAndStateIsPast(@Param("user") User user, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.startTime > CURRENT_TIMESTAMP")
    List<Booking> findAllByUserAndStateIsFuture(@Param("user") User user, Sort sort);

    List<Booking> findAllByUserAndStatus(User user, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND CURRENT_TIMESTAMP BETWEEN b.startTime AND b.endTime")
    List<Booking> findAllByOwnerAndStateIsCurrent(@Param("owner") User itemOwner, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.endTime < CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerAndStateIsPast(@Param("owner") User itemOwner, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.startTime > CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerAndStateIsFuture(@Param("owner") User itemOwner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.user= :user OR b.item.owner = :user)")
    Optional<Booking> findByIdAndUserOrOwner(@Param("bookingId") long bookingId, @Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.item = :item AND ((:start BETWEEN b.startTime AND b.endTime) " +
            "OR (:end BETWEEN b.startTime AND b.endTime))")
    List<Booking> findAllByItemAndTimeConflicts(@Param("item") Item item, @Param("start") LocalDateTime startTime,
                                                @Param("end") LocalDateTime endTime);

    Optional<Booking> findByIdAndItemOwner(long bookingId, User user);

    List<Booking> findAllByItem(Item item);

    Optional<Booking> findByItemAndUserAndEndTimeBefore(Item item, User user, LocalDateTime refTime);
}
