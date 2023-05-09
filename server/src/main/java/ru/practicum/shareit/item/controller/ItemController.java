package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
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
    public ResponseEntity<ItemDto> add(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Received POST-request at /items endpoint from user id={}", userId);
        return ResponseEntity.ok().body(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                       @PathVariable long itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Received PATCH-request at /items/{} endpoint from user id={}", itemId, userId);
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        log.info("Received GET-request at /items/{} endpoint from user id={}", itemId, userId);
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Received GET-request at /items?from={}&size={} endpoint from user id={}", from, size, userId);
        return ResponseEntity.ok().body(itemService.getOwnerItems(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam String text,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Received GET-request at /items/search?text={}&from={}&size={} endpoint from user id={}",
                text, from, size, userId);
        return ResponseEntity.ok().body(itemService.search(userId, text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                          @RequestBody CommentDto commentDto) {
        log.info("Received POST-request at /items/{}/comment endpoint from user id={}", itemId, userId);
        return ResponseEntity.ok().body(itemService.addComment(userId, itemId, commentDto));
    }

}