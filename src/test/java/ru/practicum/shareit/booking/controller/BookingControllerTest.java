package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    ItemMapper itemMapper;

    private static DateTimeFormatter formatter;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private List<BookingDto> bookingsDto;

    @BeforeAll
    static void beforeAll() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(1L)
                .name("userName1")
                .email("user1@ya.ru")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("user2@ya.ru")
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .name("Товар1")
                .description("Описание товара1")
                .available(false)
                .owner(user1)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Товар2")
                .description("Описание товара2")
                .available(false)
                .owner(user1)
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 05, 05, 9, 0, 0))
                .end(LocalDateTime.of(2023, 05, 05, 12, 0, 0))
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 05, 05, 15, 0, 0))
                .end(LocalDateTime.of(2023, 05, 05, 18, 0, 0))
                .item(item2)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto1 = bookingMapper.toBookingDto(booking1);
        bookingDto2 = bookingMapper.toBookingDto(booking2);
        bookingsDto = List.of(bookingDto1, bookingDto2);
    }

    @Test
    void getUserBookings() throws Exception {

        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingsDto);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].item.id", is((int) bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is((int) bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().format(formatter))))
                .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().format(formatter))))
                .andExpect(jsonPath("$[1].item.id", is((int) bookingDto2.getItem().getId())))
                .andExpect(jsonPath("$[1].item.name", is(bookingDto2.getItem().getName())))
                .andExpect(jsonPath("$[1].booker.id", is((int) bookingDto2.getBooker().getId())))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().name())))
        ;
    }

    @Test
    void getWrongUserBookings() throws Exception {

        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
        ;
    }

    @Test
    void getItemsOwnerBookings() throws Exception {

        when(bookingService.getItemsOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingsDto);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].item.id", is((int) bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is((int) bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().format(formatter))))
                .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().format(formatter))))
                .andExpect(jsonPath("$[1].item.id", is((int) bookingDto2.getItem().getId())))
                .andExpect(jsonPath("$[1].item.name", is(bookingDto2.getItem().getName())))
                .andExpect(jsonPath("$[1].booker.id", is((int) bookingDto2.getBooker().getId())))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().name())))
        ;
    }

    @Test
    void getBooking() throws Exception {

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto1);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is((int) bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())))
        ;
    }

    @Test
    void addNewBooking() throws Exception {

        when(bookingService.addNewBooking(anyLong(), any())).thenReturn(bookingDto1);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is((int) bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())))
        ;
    }

    @Test
    void updateBooking() throws Exception {

        when(bookingService.updateBooking(anyLong(), anyLong(), anyString())).thenReturn(bookingDto1);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto1.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is((int) bookingDto1.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())))
        ;
    }

}