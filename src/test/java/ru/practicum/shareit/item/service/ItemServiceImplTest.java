package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    BookingService bookingService;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    ItemRequestMapper requestMapper;

    private static User user;
    private static User anotherUser;
    private static Item item;
    private static Item anotherItem;
    private static List<Item> items;
    private static ItemDto itemDto;
    private static ItemDto anotherItemDto;
    private static List<ItemDto> itemsDto;
    private static Booking bookingByAnotherUser;
    private static BookingFromUserDto bookingByAnotherUserDto;
    private static Comment comment;
    private static CommentDto commentDto;
    private static ItemRequest request;
    private static ItemRequestDto requestDto;

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
                .available(false)
                .owner(user)
                .build();
        itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(new ArrayList<>());

        anotherItem = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description of item 2")
                .available(false)
                .owner(user)
                .build();
        anotherItemDto = itemMapper.toItemDto(anotherItem);
        anotherItemDto.setComments(new ArrayList<>());

        items = List.of(item, anotherItem);
        itemsDto = List.of(itemDto, anotherItemDto);

        bookingByAnotherUser = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 6, 5, 14, 52, 0))
                .end(LocalDateTime.of(2023, 6, 5, 14, 53, 0))
                .item(item)
                .booker(anotherUser)
                .status(BookingStatus.WAITING)
                .build();
        bookingByAnotherUserDto = BookingFromUserDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 6, 5, 14, 52, 0))
                .end(LocalDateTime.of(2023, 6, 5, 14, 53, 0))
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment 1 on item by Another ser")
                .item(item)
                .author(anotherUser)
                .created(LocalDateTime.of(2023, 6, 5, 15, 0, 0))
                .build();
        commentDto = commentMapper.toCommentDto(comment);

        request = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.of(2023, 6, 5, 12, 0, 0))
                .requestor(anotherUser)
                .build();
        requestDto = requestMapper.toItemRequestDto(request);
    }

    @Test
    void saveNewItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);
    }

    @Test
    void saveNewItemNoName() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        itemDto.setName(null);

        Throwable thrown = catchThrowable(() -> {
            itemService.addNewItem(user.getId(), itemDto);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewItemNoDescription() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        itemDto.setDescription(null);

        Throwable thrown = catchThrowable(() -> {
            itemService.addNewItem(user.getId(), itemDto);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewItemNoAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        itemDto.setAvailable(null);

        Throwable thrown = catchThrowable(() -> {
            itemService.addNewItem(user.getId(), itemDto);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewItemsAndUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        item.setName("New item 1");
        item.setDescription("New description of item 1");
        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto, updatedItemDto);
    }

    @Test
    void saveNewItemsAndUpdateWithNoChanges() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto, updatedItemDto);
    }

    @Test
    void saveNewItemsAndTryUpdateWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        item.setName("New item 1");
        item.setDescription("New description of item 1");
        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            itemService.updateItem(user.getId(), item.getId(), itemDto);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewItemAndUpdateWrongUserException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        item.setName("New item 1");
        item.setDescription("New description of item 1");
        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        Throwable thrown = catchThrowable(() -> {
            itemService.updateItem(user.getId(), item.getId(), itemDto);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewItemsAndGet() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto foundItemDto = itemService.getItem(user.getId(), item.getId());
        assertEquals(itemDto, foundItemDto);
    }

    @Test
    void getNoItemException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(item.getId())).thenThrow(new NotFoundException());
        Throwable thrown = catchThrowable(() -> {
            itemService.getItem(user.getId(), item.getId());
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewItemsAndGetAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        Integer from = null;
        Integer size = null;
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(items);

        List<ItemDto> ownerItems = itemService.getOwnerItems(user.getId(), from, size);
        assertEquals(itemsDto, ownerItems);
    }

    @Test
    void saveNewItemsAndGetAllWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        int from = 1;
        int size = 10;
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(items);

        List<ItemDto> ownerItems = itemService.getOwnerItems(user.getId(), from, size);
        assertEquals(itemsDto, ownerItems);
    }

    @Test
    void saveNewItemsAndGetAllWithPaginationWrongFrom() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        Integer from = -1;
        Integer size = 10;
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(items);

        Throwable thrown = catchThrowable(() -> {
            itemService.getOwnerItems(user.getId(), from, size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewItemsAndGetAllWithPaginationWrongSize() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        Integer from = 1;
        Integer size = -10;
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(items);

        Throwable thrown = catchThrowable(() -> {
            itemService.getOwnerItems(user.getId(), from, size);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void saveNewItemsAndSearchWithPagination() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        int from = 1;
        int size = 10;
        when(itemRepository.search(anyString(), eq(PageRequest.of(from / size, size)))).thenReturn(items);

        String text = "item";
        List<ItemDto> ownerItemsDto = itemService.search(user.getId(), text, from, size);
        ArrayList<CommentDto> commentsDto = new ArrayList<>();
        ownerItemsDto.forEach(itemDto -> itemDto.setComments(commentsDto));
        assertEquals(itemsDto, ownerItemsDto);
    }

    @Test
    void saveNewItemsAndSearchByEmptyText() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        int from = 1;
        int size = 10;
        when(itemRepository.search(anyString(), eq(PageRequest.of(from / size, size)))).thenReturn(items);

        String text = "";
        List<ItemDto> ownerItems = itemService.search(user.getId(), text, from, size);
        assertEquals(new ArrayList<ItemDto>(), ownerItems);
    }

    @Test
    void saveNewItemsAndSearch() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.save(anotherItem)).thenReturn(anotherItem);
        Item savedAnotherItem = itemMapper.toItem(itemService.addNewItem(user.getId(), anotherItemDto));
        assertEquals(anotherItem, savedAnotherItem);

        Integer from = null;
        Integer size = null;
        when(itemRepository.search(anyString())).thenReturn(items);

        String text = "item";
        List<ItemDto> ownerItemsDto = itemService.search(user.getId(), text, from, size);
        ArrayList<CommentDto> commentsDto = new ArrayList<>();
        ownerItemsDto.forEach(itemDto -> itemDto.setComments(commentsDto));
        assertEquals(itemsDto, ownerItemsDto);
    }

    @Test
    void createItemMakeAvailableAndAddComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto foundItemDto = itemService.getItem(user.getId(), item.getId());
        assertEquals(itemDto, foundItemDto);

        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto, updatedItemDto);

        when(bookingRepository.save(any())).thenReturn(bookingByAnotherUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        BookingDto bookingDto = bookingService.addNewBooking(anotherUser.getId(), bookingByAnotherUserDto);
        assertEquals(bookingByAnotherUser, bookingMapper.toBooking(bookingDto));

        when(bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(bookingByAnotherUser));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto addedCommentDto = itemService.addComment(anotherUser.getId(), item.getId(), commentDto);
        assertEquals(commentDto, addedCommentDto);
    }

    @Test
    void createItemMakeAvailableAndTryAddCommentWithoutBookingGetException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto foundItemDto = itemService.getItem(user.getId(), item.getId());
        assertEquals(itemDto, foundItemDto);

        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto, updatedItemDto);

        when(bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(bookingByAnotherUser));
        when(commentRepository.save(any())).thenReturn(comment);
        commentDto.setText("");
        Throwable thrown = catchThrowable(() -> {
            itemService.addComment(anotherUser.getId(), item.getId(), commentDto);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void createItemMakeAvailableAndAddEmptyCommentGetException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto foundItemDto = itemService.getItem(user.getId(), item.getId());
        assertEquals(itemDto, foundItemDto);

        item.setAvailable(true);
        itemDto = itemMapper.toItemDto(item);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto, updatedItemDto);

        when(bookingRepository.save(any())).thenReturn(bookingByAnotherUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        BookingDto bookingDto = bookingService.addNewBooking(anotherUser.getId(), bookingByAnotherUserDto);
        assertEquals(bookingByAnotherUser, bookingMapper.toBooking(bookingDto));

        when(bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(bookingByAnotherUser));
        when(commentRepository.save(any())).thenReturn(comment);
        commentDto.setText("");
        Throwable thrown = catchThrowable(() -> {
            itemService.addComment(anotherUser.getId(), item.getId(), commentDto);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void findItemById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        Item foundItem = itemService.findItem(item.getId());
        assertEquals(item, foundItem);
    }

    @Test
    void findItemByNoId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        Throwable thrown = catchThrowable(() -> {
            itemService.findItem(0);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void findItemByWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(item)).thenReturn(item);
        Item savedItem = itemMapper.toItem(itemService.addNewItem(user.getId(), itemDto));
        assertEquals(item, savedItem);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            itemService.findItem(item.getId());
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveNewItemWithRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        item.setRequest(request);
        itemDto.setRequestId(request.getId());
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        ItemDto savedItemDto = itemService.addNewItem(user.getId(), itemDto);
        savedItemDto.setComments(new ArrayList<>());
        assertEquals(itemDto, savedItemDto);
    }

}