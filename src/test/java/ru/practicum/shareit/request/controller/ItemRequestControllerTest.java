package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ItemRequestMapper itemRequestMapper;

    private static DateTimeFormatter formatter;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    private List<ItemRequestDto> requestsDto;
    private ItemRequestShortDto itemRequestShortDto;

    @BeforeAll
    static void beforeAll() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("userName1")
                .email("user1@ya.ru")
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Запрос1")
                .created(LocalDateTime.of(2023, 05, 01, 12, 0, 0))
                .requestor(user)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Запрос2")
                .created(LocalDateTime.of(2023, 05, 01, 15, 0, 0))
                .requestor(user)
                .build();
        itemRequestDto1 = itemRequestMapper.toItemRequestDto(itemRequest1);
        itemRequestDto2 = itemRequestMapper.toItemRequestDto(itemRequest2);
        requestsDto = List.of(itemRequestDto1, itemRequestDto2);

        itemRequestShortDto = ItemRequestShortDto.builder()
                .id(1L)
                .description("Запрос1")
                .build();
    }

    @Test
    void getAll() throws Exception {

        when(itemRequestService.getAll(anyLong())).thenReturn(requestsDto);

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto1.getCreated().format(formatter))))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().format(formatter))))
        ;
    }

    @Test
    void create() throws Exception {

        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestShortDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated().format(formatter))))
        ;
    }

    @Test
    void getItemRequest() throws Exception {

        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated().format(formatter))))
        ;
    }

    @Test
    void search() throws Exception {

        when(itemRequestService.search(anyLong(), anyInt(), anyInt())).thenReturn(requestsDto);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto1.getCreated().format(formatter))))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().format(formatter))))
        ;
    }

}