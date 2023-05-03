package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    private static Booking originBooking;
    private static Booking copyBooking;
    private static Booking anotherBooking;

    @BeforeAll
    static void beforeAll() {
        User user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description of item")
                .available(false)
                .owner(user)
                .build();

        originBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 1, 3, 0, 0))
                .end(LocalDateTime.of(2023, 5, 1, 6, 0, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        copyBooking = new Booking();
        copyBooking.setId(originBooking.getId());
        copyBooking.setStart(originBooking.getStart());
        copyBooking.setEnd(originBooking.getEnd());
        copyBooking.setItem(originBooking.getItem());
        copyBooking.setBooker(originBooking.getBooker());
        copyBooking.setStatus(originBooking.getStatus());

        anotherBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 5, 1, 9, 0, 0))
                .end(LocalDateTime.of(2023, 5, 1, 12, 0, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void compareEqualBookings() {
        boolean equalId = originBooking.getId().equals(copyBooking.getId());
        assertTrue(equalId);
        boolean equalStart = originBooking.getStart().equals(copyBooking.getStart());
        assertTrue(equalStart);
        boolean equalEnd = originBooking.getEnd().equals(copyBooking.getEnd());
        assertTrue(equalEnd);
        boolean equalItem = originBooking.getItem().equals(copyBooking.getItem());
        assertTrue(equalItem);
        boolean equalBooker = originBooking.getBooker().equals(copyBooking.getBooker());
        assertTrue(equalBooker);
        boolean equalStatus = originBooking.getStatus().equals(copyBooking.getStatus());
        assertTrue(equalStatus);
        boolean equal = originBooking.equals(copyBooking);
        assertTrue(equal);
    }

    @Test
    void compareDifferentBookings() {
        boolean equalId = originBooking.getId().equals(anotherBooking.getId());
        assertFalse(equalId);
        boolean equalStart = originBooking.getStart().equals(anotherBooking.getStart());
        assertFalse(equalStart);
        boolean equalEnd = originBooking.getEnd().equals(anotherBooking.getEnd());
        assertFalse(equalEnd);
        boolean equalItem = originBooking.getItem().equals(anotherBooking.getItem());
        assertTrue(equalItem);
        boolean equalBooker = originBooking.getBooker().equals(anotherBooking.getBooker());
        assertTrue(equalBooker);
        boolean equalStatus = originBooking.getStatus().equals(anotherBooking.getStatus());
        assertTrue(equalStatus);
        boolean equal = originBooking.equals(anotherBooking);
        assertFalse(equal);

        equalId = copyBooking.getId().equals(anotherBooking.getId());
        assertFalse(equalId);
        equalStart = copyBooking.getStart().equals(anotherBooking.getStart());
        assertFalse(equalStart);
        equalEnd = copyBooking.getEnd().equals(anotherBooking.getEnd());
        assertFalse(equalEnd);
        equalItem = copyBooking.getItem().equals(anotherBooking.getItem());
        assertTrue(equalItem);
        equalBooker = copyBooking.getBooker().equals(anotherBooking.getBooker());
        assertTrue(equalBooker);
        equalStatus = copyBooking.getStatus().equals(anotherBooking.getStatus());
        assertTrue(equalStatus);
        equal = copyBooking.equals(anotherBooking);
        assertFalse(equal);
    }

}