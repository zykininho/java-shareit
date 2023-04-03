package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemStorage.addNewItem(userId, item));
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
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemStorage.updateItem(userId, itemId, item));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        return itemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        List<Item> ownerItems = itemStorage.getOwnerItems(userId);
        return ownerItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        List<Item> foundItems = itemStorage.search(userId, text);
        return foundItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
