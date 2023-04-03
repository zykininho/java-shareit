package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long itemId;
    private final UserServiceImpl userService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Item addNewItem(long userId, Item item) {
        User owner = userMapper.toUser(userService.getUser(userId));
        item.setOwner(owner);
        item.setId(++this.itemId);
        items.put(item.getId(), item);
        log.info("Добавлен новый предмет: {}", item);
        return item;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        if (items.containsKey(itemId)) {
            Item itemToUpdate = items.get(itemId);
            User owner = userMapper.toUser(userService.getUser(userId));
            if (!itemToUpdate.getOwner().equals(owner)) {
                log.info("У предмета с id {} указан другой владелец {}, обращается пользователь {}",
                        itemId,
                        itemToUpdate.getOwner(),
                        owner);
                throw new NotFoundException();
            }
            if (item.getName() != null) {
                itemToUpdate.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemToUpdate.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemToUpdate.setAvailable(item.getAvailable());
            }
            return itemToUpdate;
        }
        throw new NotFoundException();
    }

    @Override
    public Item getItem(long userId, long itemId) {
        return items.getOrDefault(itemId, null);
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        List<Item> ownerItems = new ArrayList<>();
        User owner = userMapper.toUser(userService.getUser(userId));
        for (Item item : items.values()) {
            if (item.getOwner().equals(owner)) {
                ownerItems.add(item);
            }
        }
        return ownerItems;
    }

    @Override
    public List<Item> search(long userId, String text) {
        List<Item> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            if (item.getAvailable() &&
                    (StringUtils.containsIgnoreCase(item.getName(), text) ||
                            StringUtils.containsIgnoreCase(item.getDescription(), text))) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }
}
