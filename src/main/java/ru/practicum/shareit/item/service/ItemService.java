package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getOwnerItems(long userId);

    List<ItemDto> search(long userId, String text);
}
