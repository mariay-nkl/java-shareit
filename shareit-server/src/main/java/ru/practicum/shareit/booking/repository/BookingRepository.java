package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemIdAndStatusOrderByStartAsc(Long itemId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBookings(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start > ?2 " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookings(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.booker.id = ?2 AND b.end < ?3 AND b.status = 'APPROVED'")
    List<Booking> findCompletedBookings(Long itemId, Long userId, LocalDateTime now);
}