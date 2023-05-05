package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingMapperTest {

    @Autowired
    BookingMapper bookingMapper;

    private static User user;
    private static User anotherUser;
    private static Item item;
    private static ItemRequest request;
    private static Booking booking;
    private static BookingDto bookingDto;
    private static BookingForItemDto bookingForItemDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();

        anotherUser = User.builder()
                .id(2L)
                .name("Another user")
                .email("another_user@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(true)
                .owner(user)
                .request(request)
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.of(2023, 5, 5, 12, 0, 0))
                .requestor(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 6, 5, 3, 0, 0))
                .end(LocalDateTime.of(2023, 6, 5, 6, 0, 0))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = bookingMapper.toBookingDto(booking);

        bookingForItemDto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(2L)
                .build();
    }

    @Test
    void createBookingDtoFromBooking() {
        BookingDto bookingDtoFromMapper = BookingMapper.INSTANCE.toBookingDto(booking);
        assertEquals(bookingDto, bookingDtoFromMapper);
    }

    @Test
    void createBookingForItemDtoFromBooking() {
        BookingForItemDto bookingForItemDtoFromMapper = BookingMapper.INSTANCE.toBookingForItemDto(booking);
        assertEquals(bookingForItemDto, bookingForItemDtoFromMapper);
    }

    @Test
    void createBookingFromBookingDto() {
        Booking bookingFromMapper = BookingMapper.INSTANCE.toBooking(bookingDto);
        assertEquals(booking, bookingFromMapper);
    }

}