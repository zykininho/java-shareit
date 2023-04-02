package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoreUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private long userId;

    @Override
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        user.setId(++this.userId);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(long userId, User user) {
        if (!users.containsKey(userId)) {
            log.info("Не найден пользователь в списке с id: {}", userId);
            throw new NotFoundException();
        }
        User userToUpdate = users.get(userId);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        users.put(userId, userToUpdate);
        log.info("Обновлены данные пользователя с id {}. Новые данные: {}", userId, userToUpdate);
        return userToUpdate;
    }

    @Override
    public User getUser(long userId) {
        if (userId == 0) {
            throw new ValidationException();
        } else if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
