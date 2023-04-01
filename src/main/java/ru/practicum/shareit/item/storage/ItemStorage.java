package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.UserDto;

import java.util.List;

public interface ItemStorage {

    UserDto addNewItem(Long userId, UserDto itemDto);

    UserDto addNewItem(Long userId, long itemId);

    UserDto getItem(long userId, long itemId);

    List<UserDto> getOwnerItems(long userId);

    List<UserDto> search(long userId, String text);
}
