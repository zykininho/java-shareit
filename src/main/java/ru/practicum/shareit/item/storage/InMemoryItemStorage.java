package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.model.User;

import java.util.HashMap;
import java.util.List;

public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, User> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public UserDto addNewItem(Long userId, UserDto itemDto) {
        return null;
    }

    @Override
    public UserDto addNewItem(Long userId, long itemId) {
        return null;
    }

    @Override
    public UserDto getItem(long userId, long itemId) {
        return null;
    }

    @Override
    public List<UserDto> getOwnerItems(long userId) {
        return null;
    }

    @Override
    public List<UserDto> search(long userId, String text) {
        return null;
    }
}
