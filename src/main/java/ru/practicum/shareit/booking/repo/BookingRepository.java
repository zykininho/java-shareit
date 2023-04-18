package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findByBookerAndStatusIs(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIs(User booker, Sort sort);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndEndIsBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerAndStartIsAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerAndStatusIs(User booker, BookingStatus status, Sort sort);

    Booking findFirst1ByItemIdIsAndStartIsBeforeAndStatusIsOrderByEndDesc(long itemId, LocalDateTime end, BookingStatus status);

    Booking findFirst1ByItemIdIsAndStartIsAfterAndStatusIsOrderByStartAsc(long itemId, LocalDateTime start, BookingStatus status);

    Booking findFirst1ByItemIdAndBookerIdAndEndIsBefore(long itemId, long bookerId, LocalDateTime end);

}