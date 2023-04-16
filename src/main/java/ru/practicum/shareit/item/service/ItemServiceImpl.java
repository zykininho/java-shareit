package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemRepository itemRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    private final UserService userService;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = itemMapper.toItem(itemDto);
//        return itemMapper.toItemDto(itemStorage.addNewItem(userId, item));
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
//        return itemMapper.toItemDto(itemStorage.updateItem(userId, itemId, item));
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
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
//        return itemMapper.toItemDto(itemStorage.getItem(userId, itemId));
        if (itemId == 0) {
            throw new ValidationException();
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException();
        }
        return itemMapper.toItemDto(item.get());
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
//        List<Item> ownerItems = itemStorage.getOwnerItems(userId);
        List<Item> ownerItems = itemRepository.findAllByOwnerId(userId);
        return ownerItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
//        List<Item> foundItems = itemStorage.search(userId, text);
        List<Item> foundItems =
                itemRepository.findAllByAvailableAndNameOrDescriptionContainingIgnoreCase(true, text);
        return foundItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}