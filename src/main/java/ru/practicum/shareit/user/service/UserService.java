package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        validateToCreate(user);
        return userStorage.create(user);
    }

    private void validateToCreate(User user) {
        String email = user.getEmail();
        List<User> users = getAll();
        for (User userToCheck : users) {
            if (email == null) {
                log.info("У пользователя не задана электронная почта");
                throw new ValidationException();
            } else if (!email.contains("@")) {
                log.info("У пользователя указана неверная электронная почта");
                throw new ValidationException();
            } else if (userToCheck.getEmail().equals(user.getEmail())) {
                log.info("В системе уже есть пользователь c id {} с такой же почтой {}",
                        userToCheck.getId(),
                        userToCheck.getEmail());
                throw new ConflictException();
            }
        }
    }

    public User update(long userId, User user) {
        validateToUpdate(userId, user);
        return userStorage.update(userId, user);
    }

    private void validateToUpdate(long userId, User user) {
        String email = user.getEmail();
        if (email != null && !email.contains("@")) {
            log.info("У пользователя указана неверная электронная почта");
            throw new ValidationException();
        }
        List<User> users = getAll();
        for (User userToCheck : users) {
            if (userToCheck.getId() != userId && userToCheck.getEmail().equals(email)) {
                log.info("В системе уже есть пользователь c id {} с такой же почтой {}",
                        userToCheck.getId(),
                        userToCheck.getEmail());
                throw new ConflictException();
            }
        }
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
