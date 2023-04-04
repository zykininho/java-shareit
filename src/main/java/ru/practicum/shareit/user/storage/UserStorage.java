package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User create(User user);

    User update(long userId, User user);

    User getUser(long userId);

    void deleteUser(long userId);
}
