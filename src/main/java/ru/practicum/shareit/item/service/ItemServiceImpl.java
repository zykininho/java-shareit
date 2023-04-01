package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public UserDto addNewItem(Long userId, UserDto itemDto) {
        return itemStorage.addNewItem(userId, itemDto);
    }

    @Override
    public UserDto addNewItem(Long userId, long itemId) {
        return itemStorage.addNewItem(userId, itemId);
    }

    @Override
    public UserDto getItem(long userId, long itemId) {
        return itemStorage.getItem(userId, itemId);
    }

    @Override
    public List<UserDto> getOwnerItems(long userId) {
        return itemStorage.getOwnerItems(userId);
    }

    @Override
    public List<UserDto> search(long userId, String text) {
        return itemStorage.search(userId, text);
    }
}
