package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public UserDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody UserDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public UserDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId) {
        return itemService.addNewItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public UserDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<UserDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<UserDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text) {
        return itemService.search(userId, text);
    }
}
