package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
    private final UserService userService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    private final BookingRepository bookingRepository;
    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = itemMapper.toItem(itemDto);
        User owner = userMapper.toUser(userService.getUser(userId));
        item.setOwner(owner);
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
        User owner = userMapper.toUser(userService.getUser(userId));
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
        return itemDto;
    }

    private void addBookings(ItemDto itemDto) {
        long itemId = itemDto.getId();
        Booking lastBooking = bookingRepository.findFirst1ByItemIdIsAndEndIsBeforeOrderByEndDesc(
                itemId,
                LocalDateTime.now());
        itemDto.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking));
        Booking nextBooking = bookingRepository.findFirst1ByItemIdIsAndStartIsAfterOrderByStartAsc(
                itemId,
                LocalDateTime.now());
        itemDto.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        List<Item> ownerItems = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : ownerItems) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            if (item.getOwner().getId().equals(userId)) {
                addBookings(itemDto);
            }
            listItemDto.add(itemDto);
        }
        return listItemDto;
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems = itemRepository.search(text);
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

}