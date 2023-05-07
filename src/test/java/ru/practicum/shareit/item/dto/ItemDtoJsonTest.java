package ru.practicum.shareit.item.dto;

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
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTest
public class ItemDtoJsonTest {

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
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        BookingForItemDto lastBooking = BookingForItemDto.builder()
                .id(1L)
                .bookerId(2L)
                .build();

        BookingForItemDto nextBooking = BookingForItemDto.builder()
                .id(2L)
                .bookerId(3L)
                .build();

        CommentDto commentFromUser1 = CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .authorName("User 1")
                .created(LocalDateTime.now().minusDays(2))
                .build();

        CommentDto commentFromUser2 = CommentDto.builder()
                .id(2L)
                .text("Comment 2")
                .authorName("User 2")
                .created(LocalDateTime.now().minusDays(1))
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description of item")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(commentFromUser1, commentFromUser2))
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description of item");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Comment 1");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("User 1");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text").isEqualTo("Comment 2");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName").isEqualTo("User 2");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}