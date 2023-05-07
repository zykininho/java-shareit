package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTest
public class BookingDtoJsonTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    CommentRepository commentRepository;

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testItemDto() throws Exception {
        UserShortDto userDto = UserShortDto.builder()
                .id(1L)
                .build();

        ItemShortDto itemDto = ItemShortDto.builder()
                .id(1L)
                .name("Item")
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 1, 3, 0, 0))
                .end(LocalDateTime.of(2023, 5, 1, 6, 0, 0))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
    }

}