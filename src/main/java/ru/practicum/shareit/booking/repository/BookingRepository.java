package ru.practicum.shareit.booking.repository;

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
    List<Booking> findAllByUserOrderByStartTimeDesc(User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.startTime <= CURRENT_TIMESTAMP " +
            "AND b.endTime >= CURRENT_TIMESTAMP ORDER BY b.startTime DESC")
    List<Booking> findAllByUserAndStateIsCurrentOrderByStartTimeDesc(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.endTime < CURRENT_TIMESTAMP ORDER BY b.startTime DESC")
    List<Booking> findAllByUserAndStateIsPastOrderByStartTimeDesc(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.startTime > CURRENT_TIMESTAMP ORDER BY b.startTime DESC")
    List<Booking> findAllByUserAndStateIsFutureOrderByStartTimeDesc(@Param("user") User user);

    List<Booking> findAllByUserAndStatusOrderByStartTimeDesc(User user, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartTimeDesc(User itemOwner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.startTime <= CURRENT_TIMESTAMP " +
           "AND b.endTime >= CURRENT_TIMESTAMP ORDER BY b.startTime DESC")
    List<Booking> findAllByOwnerAndStateIsCurrentOrderByStartTimeDesc(@Param("owner") User itemOwner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.endTime < CURRENT_TIMESTAMP " +
           "ORDER BY b.startTime DESC")
    List<Booking> findAllByOwnerAndStateIsPastOrderByStartTimeDesc(@Param("owner") User itemOwner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.startTime > CURRENT_TIMESTAMP " +
           "ORDER BY b.startTime DESC")
    List<Booking> findAllByOwnerAndStateIsFutureOrderByStartTimeDesc(@Param("owner") User itemOwner);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartTimeDesc(User itemOwner, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.user= :user OR b.item.owner = :user)")
    Optional<Booking> findByIdAndUserOrOwner(@Param("bookingId") long bookingId, @Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.item = :item AND ((b.startTime <= :start AND b.endTime >= :start) " +
           "OR (b.startTime <= :end AND b.endTime >= :end))")
    List<Booking> findAllByItemAndTimeConflicts(@Param("item") Item item, @Param("start") LocalDateTime startTime,
                                                @Param("end") LocalDateTime endTime);

    Optional<Booking> findByIdAndItemOwner(long bookingId, User user);

    List<Booking> findAllByItem(Item item);

    Optional<Booking> findByItemIdAndUserIdAndEndTimeBefore(long itemId, long userId, LocalDateTime refTime);
}
