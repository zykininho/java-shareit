package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestMapperTest {

    @Autowired
    ItemRequestMapper itemRequestMapper;

    private static ItemRequest request;
    private static ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.of(2023, 5, 5, 12, 0, 0))
                .requestor(user)
                .build();
        requestDto = itemRequestMapper.toItemRequestDto(request);
    }

    @Test
    void createItemRequestDtoFromItemRequest() {
        ItemRequestDto itemRequestDtoFromMapper = ItemRequestMapper.INSTANCE.toItemRequestDto(request);
        assertEquals(requestDto, itemRequestDtoFromMapper);
    }

    @Test
    void createItemRequestFromItemRequestDto() {
        ItemRequest itemRequestFromMapper = ItemRequestMapper.INSTANCE.toItemRequest(requestDto);
        assertEquals(request, itemRequestFromMapper);
    }

}