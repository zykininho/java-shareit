package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addNewItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    Item getItem(long userId, long itemId);

    List<Item> getOwnerItems(long userId);

    List<Item> search(long userId, String text);
}
