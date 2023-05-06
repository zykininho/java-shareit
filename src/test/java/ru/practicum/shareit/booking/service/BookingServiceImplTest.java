package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {

    @Autowired
    BookingService bookingService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    @Autowired
    ItemRequestMapper requestMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    BookingMapper bookingMapper;

    private static User user;
    private static User anotherUser;
    private static User userForCheck;
    private static ItemRequest request;
    private static ItemRequestDto requestDto;
    private static ItemRequest anotherRequest;
    private static ItemRequestDto anotherRequestDto;
    private static ItemRequestShortDto requestFromUser;
    private static ItemRequestShortDto anotherRequestFromUser;
    private static Item item;
    private static Item anotherItem;
    private static ItemDto itemDto;
    private static ItemDto anotherItemDto;
    private static List<Item> items;
    private static List<ItemDto> itemsDto;
    private static List<ItemRequest> requests;
    private static List<ItemRequestDto> requestsDto;
    private static Booking booking;
    private static Booking anotherBooking;
    private static List<Booking> bookings;
    private static BookingDto bookingDto;
    private static BookingDto anotherBookingDto;
    private static List<BookingDto> bookingsDto;
    private static BookingFromUserDto bookingFromUser;
    private static BookingFromUserDto anotherBookingFromUser;

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

        userForCheck = User.builder()
                .id(3L)
                .name("User for check")
                .email("user_for_check@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemDto = itemMapper.toItemDto(item);

        anotherItem = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description of item 2")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        anotherItemDto = itemMapper.toItemDto(anotherItem);

        items = List.of(item, anotherItem);
        itemsDto = List.of(itemDto, anotherItemDto);

        request = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.of(2023, 5, 5, 12, 0, 0))
                .requestor(user)
                .build();
        requestDto = requestMapper.toItemRequestDto(request);

        requestFromUser = ItemRequestShortDto.builder()
                .id(1L)
                .description("Request 1")
                .build();

        anotherRequest = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .created(LocalDateTime.of(2023, 5, 5, 15, 0, 0))
                .requestor(user)
                .build();
        anotherRequestDto = requestMapper.toItemRequestDto(anotherRequest);

        anotherRequestFromUser = ItemRequestShortDto.builder()
                .id(2L)
                .description("Request 2")
                .build();

        requests = List.of(request, anotherRequest);
        requestsDto = List.of(requestDto, anotherRequestDto);

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 6, 5, 3, 0, 0))
                .end(LocalDateTime.of(2023, 6, 5, 6, 0, 0))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = bookingMapper.toBookingDto(booking);

        bookingFromUser = BookingFromUserDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 6, 5, 3, 0, 0))
                .end(LocalDateTime.of(2023, 6, 5, 6, 0, 0))
                .build();

        anotherBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 6, 5, 9, 0, 0))
                .end(LocalDateTime.of(2023, 6, 5, 12, 0, 0))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.WAITING)
                .build();
        anotherBookingDto = bookingMapper.toBookingDto(anotherBooking);

        anotherBookingFromUser = BookingFromUserDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 6, 5, 9, 0, 0))
                .end(LocalDateTime.of(2023, 6, 5, 12, 0, 0))
                .build();

        bookings = List.of(booking, anotherBooking);
        bookingsDto = List.of(bookingDto, anotherBookingDto);
    }

    @Test
    void saveNewBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);
    }

    @Test
    void saveNewBookingNoDate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setStart(null);
        bookingFromUser.setEnd(null);
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingEqualDate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setStart(bookingFromUser.getEnd());
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingStartAfterEnd() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setStart(bookingFromUser.getEnd().plusDays(1));
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingStartBeforeNow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setStart(LocalDateTime.now().minusDays(1));
        bookingFromUser.setEnd(LocalDateTime.now());
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingEndBeforeNow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setStart(LocalDateTime.now());
        bookingFromUser.setEnd(LocalDateTime.now().minusDays(1));
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingItemIdIsNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        bookingFromUser.setItemId(null);
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewBookingItemIsNotAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        item.setAvailable(false);
        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingBookerIsOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);

        Throwable thrown = catchThrowable(() -> {
            bookingService.addNewBooking(user.getId(), bookingFromUser);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewBookingAndUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto updateBooking = bookingService.updateBooking(user.getId(), booking.getId(), "true");
        savedBooking.setStatus(BookingStatus.APPROVED);
        assertEquals(updateBooking, savedBooking);
    }

    @Test
    void saveNewBookingAndUpdateWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(user.getId(), 0, "true");
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingAndUpdateNoBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(user.getId(), booking.getId(), "true");
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewBookingAndUpdateStatusByNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(anotherUser.getId(), booking.getId(), "true");
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewBookingAndRejectByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto updateBooking = bookingService.updateBooking(user.getId(), booking.getId(), "false");
        savedBooking.setStatus(BookingStatus.REJECTED);
        assertEquals(updateBooking, savedBooking);
    }

    @Test
    void saveNewBookingAndUpdateAlreadyApproved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);
        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(user.getId(), booking.getId(), "true");
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingAndUpdateAlreadyRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        booking.setStatus(BookingStatus.REJECTED);
        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(user.getId(), booking.getId(), "false");
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewBookingAndUpdateWithWrongStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Throwable thrown = catchThrowable(() -> {
            bookingService.updateBooking(user.getId(), booking.getId(), "none");
        });
        assertThat(thrown).isInstanceOf(RuntimeException.class);
    }

    @Test
    void saveNewBookingAndGetByBooker() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        BookingDto foundBookingDto = bookingService.getBooking(anotherUser.getId(), booking.getId());
        assertEquals(bookingDto, foundBookingDto);
    }

    @Test
    void saveNewBookingAndGetByItemOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        BookingDto foundBookingDto = bookingService.getBooking(user.getId(), booking.getId());
        assertEquals(bookingDto, foundBookingDto);
    }

    @Test
    void saveNewBookingAndGetByWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userForCheck));

        Throwable thrown = catchThrowable(() -> {
            bookingService.getBooking(userForCheck.getId(), booking.getId());
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getStateAllUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findAllByBooker(any(), (Pageable) any())).thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "ALL",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateAllUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findAllByBooker(any(), (Sort) any())).thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "ALL",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getWrongStateUserBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findAllByBooker(any(), (Pageable) any())).thenReturn(bookings);

        Throwable thrown = catchThrowable(() -> {
            bookingService.getUserBookings(
                    anotherUser.getId(),
                    "None",
                    from,
                    size);
        });
        assertThat(thrown).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getStateAllUserBookingsWrongPageFrom() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = -1;
        Integer size = 10;
        when(bookingRepository.findAllByBooker(any(), (Pageable) any())).thenReturn(bookings);

        Throwable thrown = catchThrowable(() -> {
            bookingService.getUserBookings(
                    anotherUser.getId(),
                    "ALL",
                    from,
                    size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void getStateAllUserBookingsWrongPageSize() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = -1;
        when(bookingRepository.findAllByBooker(any(), (Pageable) any())).thenReturn(bookings);

        Throwable thrown = catchThrowable(() -> {
            bookingService.getUserBookings(
                    anotherUser.getId(),
                    "ALL",
                    from,
                    size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void getStateCurrentUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "CURRENT",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateCurrentUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "CURRENT",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStatePastUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByBookerAndEndIsBefore(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "PAST",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStatePastUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByBookerAndEndIsBefore(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "PAST",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateFutureUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByBookerAndStartIsAfter(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "FUTURE",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateFutureUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByBookerAndStartIsAfter(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "FUTURE",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateWaitingUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByBookerAndStatusIs(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "WAITING",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateWaitingUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByBookerAndStatusIs(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "WAITING",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateRejectedUserBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByBookerAndStatusIs(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "REJECTED",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateRejectedUserBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByBookerAndStatusIs(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getUserBookings(
                anotherUser.getId(),
                "REJECTED",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateAllItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findAllByItemOwnerIs(any(), (Pageable) any())).thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "ALL",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateAllItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findAllByItemOwnerIs(any(), (Sort) any())).thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "ALL",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getWrongStateItemsOwnerBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findAllByItemOwnerIs(any(), (Pageable) any())).thenReturn(bookings);

        Throwable thrown = catchThrowable(() -> {
            bookingService.getItemsOwnerBookings(
                    anotherUser.getId(),
                    "None",
                    from,
                    size);
        });
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertEquals(thrown.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getStateCurrentItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "CURRENT",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateCurrentItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "CURRENT",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStatePastItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByItemOwnerAndEndIsBefore(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "PAST",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStatePastItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByItemOwnerAndEndIsBefore(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "PAST",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateFutureItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByItemOwnerAndStartIsAfter(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "FUTURE",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateFutureItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByItemOwnerAndStartIsAfter(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "FUTURE",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateWaitingItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByItemOwnerAndStatusIs(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "WAITING",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateWaitingItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByItemOwnerAndStatusIs(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "WAITING",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateRejectedItemsOwnerBookingsWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = 1;
        Integer size = 10;
        when(bookingRepository.findByItemOwnerAndStatusIs(any(), any(), (Pageable) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "REJECTED",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

    @Test
    void getStateRejectedItemsOwnerBookingsWithSort() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto savedBooking = bookingService.addNewBooking(anotherUser.getId(), bookingFromUser);
        assertEquals(bookingDto, savedBooking);

        when(bookingRepository.save(any())).thenReturn(anotherBooking);
        BookingDto savedAnotherBooking = bookingService.addNewBooking(anotherUser.getId(), anotherBookingFromUser);
        assertEquals(anotherBookingDto, savedAnotherBooking);

        Integer from = null;
        Integer size = null;
        when(bookingRepository.findByItemOwnerAndStatusIs(any(), any(), (Sort) any()))
                .thenReturn(bookings);
        List<BookingDto> foundBookingsDto = bookingService.getItemsOwnerBookings(
                anotherUser.getId(),
                "REJECTED",
                from,
                size);
        assertEquals(bookingsDto, foundBookingsDto);
    }

}