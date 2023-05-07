package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getOwnerItems(long userId, Integer from, Integer size);

    List<ItemDto> search(long userId, String text, Integer from, Integer size);

    Item findItem(long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

}