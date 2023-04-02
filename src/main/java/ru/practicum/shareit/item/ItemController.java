package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Received POST-request at /items endpoint from user id={}", userId);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto add(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                       @PathVariable long itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Received PATCH-request at /items/{} endpoint from user id={}", itemId, userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        log.info("Received GET-request at /items/{} endpoint from user id={}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET-request at /items endpoint from user id={}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text) {
        log.info("Received GET-request at /items/search?text={} endpoint from user id={}", text, userId);
        return itemService.search(userId, text);
    }
}
