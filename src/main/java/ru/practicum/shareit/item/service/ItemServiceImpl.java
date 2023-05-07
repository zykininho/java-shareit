package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = itemMapper.toItem(itemDto);
        User owner = findUser(userId);
        item.setOwner(owner);
        long requestId = itemDto.getRequestId();
        if (requestId != 0) {
            ItemRequest itemRequest = itemRequestService.findById(requestId);
            item.setRequest(itemRequest);
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("В предмете {} не указано наименование", itemDto);
            throw new ValidationException();
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("В предмете {} не указано описание", itemDto);
            throw new ValidationException();
        } else if (itemDto.getAvailable() == null) {
            log.info("В предмете {} не указан статус доступности", itemDto);
            throw new ValidationException();
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException();
        }
        Item itemToUpdate = optionalItem.get();
        User owner = findUser(userId);
        if (!itemToUpdate.getOwner().equals(owner)) {
            log.info("У предмета с id {} указан другой владелец {}, обращается пользователь {}",
                    itemId,
                    itemToUpdate.getOwner(),
                    owner);
            throw new NotFoundException();
        }
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        Item savedItem = itemRepository.save(itemToUpdate);
        log.info("Обновлен item: {}", savedItem);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        if (itemId == 0) {
            throw new ValidationException();
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException();
        }
        Item item = optionalItem.get();
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            addBookings(itemDto);
        }
        addComments(itemDto);
        return itemDto;
    }

    private void addBookings(ItemDto itemDto) {
        long itemId = itemDto.getId();
        Optional<Booking> lastBooking = bookingRepository.findFirst1ByItemIdIsAndStartIsBeforeAndStatusIsOrderByEndDesc(
                itemId,
                LocalDateTime.now(),
                BookingStatus.APPROVED);
        itemDto.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking.orElse(null)));
        Optional<Booking> nextBooking = bookingRepository.findFirst1ByItemIdIsAndStartIsAfterAndStatusIsOrderByStartAsc(
                itemId,
                LocalDateTime.now(),
                BookingStatus.APPROVED);
        itemDto.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking.orElse(null)));
    }

    private void addComments(ItemDto itemDto) {
        long itemId = itemDto.getId();
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> listCommentDto = comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(listCommentDto);
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId, Integer from, Integer size) {
        List<Item> ownerItems;
        if (from != null && size != null) {
            validateSearchParameters(from, size);
            ownerItems = itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size));
        } else {
            ownerItems = itemRepository.findAllByOwnerId(userId);
        }
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : ownerItems) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            if (item.getOwner().getId().equals(userId)) {
                addBookings(itemDto);
            }
            addComments(itemDto);
            listItemDto.add(itemDto);
        }
        return listItemDto;
    }

    @Override
    public List<ItemDto> search(long userId, String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems;
        if (from != null && size != null) {
            validateSearchParameters(from, size);
            foundItems = itemRepository.search(text, PageRequest.of(from / size, size));
        } else {
            foundItems = itemRepository.search(text);
        }
        return foundItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Item findItem(long itemId) {
        if (itemId == 0) {
            throw new ValidationException();
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException();
        }
        return item.get();
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = findUser(userId);
        Item item = findItem(itemId);
        Optional<Booking> booking = bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now());
        if (booking.isEmpty()) {
            log.info("Пользователь с id={} не может добавить отзыв к товару с id={}, он не бронировал его",
                    userId, itemId);
            throw new ValidationException();
        }
        String text = commentDto.getText();
        if (text == null || text.isBlank()) {
            log.info("В отзыве от пользователя с id={} на товар с id={} нет текста",
                    userId, itemId);
            throw new ValidationException();
        }
        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Сохранен новый отзыв {} от пользователя с id={} на товар с id={} текста",
                savedComment,
                userId,
                itemId);
        return commentMapper.toCommentDto(savedComment);
    }

    private User findUser(long userId) {
        if (userId == 0) {
            throw new ValidationException();
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return user.get();
    }

    private void validateSearchParameters(int from, int size) {
        if (from < 0) {
            log.info("Параметр запроса 'from' должен быть больше или равен 0, указано значение {}", from);
            throw new ValidationException();
        } else if (size <= 0) {
            log.info("Параметр запроса 'size' должен быть больше 0, указано значение {}", size);
            throw new ValidationException();
        }
    }

}