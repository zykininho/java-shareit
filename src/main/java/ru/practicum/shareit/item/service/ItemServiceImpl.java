package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item = itemStorage.addNewItem(userId, item);
        return ItemMapper.toItemDto(item);
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("В предмете не указано наименование");
            throw new ValidationException();
        } else if (itemDto.getAvailable() == null) {
            log.info("В предмете не указан статус доступности");
            throw new ValidationException();
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("В предмете не указано описание");
            throw new ValidationException();
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item = itemStorage.updateItem(userId, itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        List<Item> ownerItems = itemStorage.getOwnerItems(userId);
        List<ItemDto> ownerItemsDto = new ArrayList<>();
        for (Item ownerItem : ownerItems) {
            ItemDto itemDto = ItemMapper.toItemDto(ownerItem);
            ownerItemsDto.add(itemDto);
        }
        return ownerItemsDto;
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        List<Item> foundItems = itemStorage.search(userId, text);
        List<ItemDto> foundItemsDto = new ArrayList<>();
        for (Item foundItem : foundItems) {
            ItemDto itemDto = ItemMapper.toItemDto(foundItem);
            foundItemsDto.add(itemDto);
        }
        return foundItemsDto;
    }

}
