package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Received GET-request at /users endpoint");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") long userId) {
        log.info("Received GET-request at /users/{} endpoint", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Received POST-request at /users endpoint");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long userId, @RequestBody UserDto userDto) {
        log.info("Received PATCH-request at /users/{} endpoint", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        log.info("Received DELETE-request at /users/{} endpoint", userId);
        userService.deleteUser(userId);
    }
}
