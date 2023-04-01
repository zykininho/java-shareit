package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User create(User user);

    User update(User user);

    User getUser(Integer userId);

    void deleteAll();

    void deleteUser(Integer userId);
}
