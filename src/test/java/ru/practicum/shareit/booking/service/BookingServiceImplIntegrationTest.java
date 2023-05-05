package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Test
    void saveItem() {
        User user = makeUser("User", "user@ya.ru");
        UserDto userDto = userMapper.toUserDto(user);
        Item item = makeItem("Item 1", "Description of item 1", true, user);
        ItemDto itemDto = itemMapper.toItemDto(item);

        userDto = userService.create(userDto);
        itemDto = itemService.addNewItem(userDto.getId(), itemDto);

        User booker = makeUser("Booker", "booker@ya.ru");
        UserDto bookerDto = userMapper.toUserDto(booker);
        bookerDto = userService.create(bookerDto);

        ItemShortDto itemShortDto = ItemShortDto.builder().id(itemDto.getId()).name(itemDto.getName()).build();

        BookingFromUserDto bookingDto = makeBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemShortDto
        );
        BookingDto booking = bookingService.addNewBooking(bookerDto.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :bookerId", Booking.class);
        Booking savedBooking = query.setParameter("bookerId", bookerDto.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(savedBooking.getId()));
        assertThat(booking.getStart(), equalTo(savedBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(savedBooking.getEnd()));
        assertThat(booking.getBooker().getId(), equalTo(savedBooking.getBooker().getId()));
    }

    private Item makeItem(String name, String description, boolean available, User owner) {
        return Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }

    private User makeUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private BookingFromUserDto makeBookingDto(LocalDateTime start, LocalDateTime end, ItemShortDto item) {
        return BookingFromUserDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();
    }

}