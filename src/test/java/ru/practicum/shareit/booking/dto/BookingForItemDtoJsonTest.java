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
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.repo.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTest
public class BookingForItemDtoJsonTest {

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
    private JacksonTester<BookingForItemDto> json;

    @Test
    void testBookingForItemDto() throws Exception {
        BookingForItemDto bookingDto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookingForItemDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

}