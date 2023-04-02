package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        log.info("Received GET-request at /users endpoint");
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Received POST-request at /users endpoint");
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable("id") long userId, @Valid @RequestBody User user) {
        log.info("Received PATCH-request at /users/{} endpoint", userId);
        return userService.update(userId, user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") long userId) {
        log.info("Received GET-request at /users/{} endpoint", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        log.info("Received DELETE-request at /users/{} endpoint", userId);
        userService.deleteUser(userId);
    }
}
