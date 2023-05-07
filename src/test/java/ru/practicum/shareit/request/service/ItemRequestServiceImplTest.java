package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ItemRequestServiceImplTest {

    @Autowired
    ItemRequestService requestService;

    @MockBean
    ItemRequestRepository requestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @Autowired
    ItemRequestMapper requestMapper;

    @Autowired
    ItemMapper itemMapper;

    private static User user;
    private static ItemRequest request;
    private static ItemRequestDto requestDto;
    private static ItemRequest anotherRequest;
    private static ItemRequestDto anotherRequestDto;
    private static ItemRequestShortDto requestFromUser;
    private static ItemRequestShortDto anotherRequestFromUser;
    private static List<Item> items;
    private static List<ItemDto> itemsDto;
    private static List<ItemRequest> requests;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(false)
                .owner(user)
                .request(request)
                .build();
        ItemDto itemDto = itemMapper.toItemDto(item);

        Item anotherItem = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description of item 2")
                .available(false)
                .owner(user)
                .request(request)
                .build();
        ItemDto anotherItemDto = itemMapper.toItemDto(anotherItem);

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
    }

    @Test
    void saveNewRequest() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);
    }

    @Test
    void saveNewRequestNoDescriptionException() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        requestFromUser.setDescription(null);
        Throwable thrown = catchThrowable(() -> {
            requestService.create(user.getId(), requestFromUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewRequestAndGet() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        ItemRequestDto foundRequestDto = requestService.getItemRequest(user.getId(), request.getId());
        requestDto.setItems(itemsDto);
        assertEquals(requestDto, foundRequestDto);
    }

    @Test
    void getNoRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new NotFoundException());
        Throwable thrown = catchThrowable(() -> {
            requestService.getItemRequest(user.getId(), request.getId());
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getWrongIdRequest() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        Throwable thrown = catchThrowable(() -> {
            requestService.getItemRequest(user.getId(), 0);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewRequestsAndGetAll() {
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.save(any())).thenReturn(anotherRequest);
        ItemRequestDto savedAnotherRequestDto = requestService.create(user.getId(), anotherRequestFromUser);
        assertEquals(anotherRequestDto, savedAnotherRequestDto);

        when(requestRepository.findAllByRequestorId(anyLong(), any())).thenReturn(requests);
        List<ItemRequestDto> foundRequestsDto = requestService.getAll(user.getId());
        requestDto.setItems(itemsDto);
        anotherRequestDto.setItems(new ArrayList<>());
        assertEquals(List.of(requestDto, anotherRequestDto), foundRequestsDto);
    }

    @Test
    void saveNewRequestsAndSearchWithPageable() {
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.save(any())).thenReturn(anotherRequest);
        ItemRequestDto savedAnotherRequestDto = requestService.create(user.getId(), anotherRequestFromUser);
        assertEquals(anotherRequestDto, savedAnotherRequestDto);

        int from = 1;
        int size = 10;
        when(requestRepository.findAllByOtherRequestors(anyLong(), (Pageable) any())).thenReturn(requests);
        List<ItemRequestDto> foundRequestsDto = requestService.search(user.getId(), from, size);
        requestDto.setItems(itemsDto);
        anotherRequestDto.setItems(new ArrayList<>());
        assertEquals(List.of(requestDto, anotherRequestDto), foundRequestsDto);
    }

    @Test
    void saveNewRequestsAndSearchWithSort() {
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.save(any())).thenReturn(anotherRequest);
        ItemRequestDto savedAnotherRequestDto = requestService.create(user.getId(), anotherRequestFromUser);
        assertEquals(anotherRequestDto, savedAnotherRequestDto);

        Integer from = null;
        Integer size = null;
        when(requestRepository.findAllByOtherRequestors(anyLong(), (Sort) any())).thenReturn(requests);
        List<ItemRequestDto> foundRequestsDto = requestService.search(user.getId(), from, size);
        requestDto.setItems(itemsDto);
        anotherRequestDto.setItems(new ArrayList<>());
        assertEquals(List.of(requestDto, anotherRequestDto), foundRequestsDto);
    }

    @Test
    void saveNewRequestsAndSearchWithWrongFromPageable() {
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.save(any())).thenReturn(anotherRequest);
        ItemRequestDto savedAnotherRequestDto = requestService.create(user.getId(), anotherRequestFromUser);
        assertEquals(anotherRequestDto, savedAnotherRequestDto);

        Integer from = -1;
        Integer size = 10;
        when(requestRepository.findAllByOtherRequestors(anyLong(), (Sort) any())).thenReturn(requests);
        Throwable thrown = catchThrowable(() -> {
            requestService.search(user.getId(), from, size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewRequestsAndSearchWithWrongSizePageable() {
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.save(any())).thenReturn(anotherRequest);
        ItemRequestDto savedAnotherRequestDto = requestService.create(user.getId(), anotherRequestFromUser);
        assertEquals(anotherRequestDto, savedAnotherRequestDto);

        Integer from = 1;
        Integer size = -1;
        when(requestRepository.findAllByOtherRequestors(anyLong(), (Sort) any())).thenReturn(requests);
        Throwable thrown = catchThrowable(() -> {
            requestService.search(user.getId(), from, size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewRequestAndFindById() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        ItemRequest foundRequest = requestService.findById(request.getId());
        assertEquals(request, foundRequest);
    }

    @Test
    void saveNewRequestAndFindByWrongIdException() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto savedRequestDto = requestService.create(user.getId(), requestFromUser);
        assertEquals(requestDto, savedRequestDto);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            requestService.findById(99);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

}