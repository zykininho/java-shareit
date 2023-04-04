package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    @Autowired
    private UserMapper userMapper;

    public List<UserDto> getAll() {
        return userStorage.getAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validateToCreate(user);
        return userMapper.toUserDto(userStorage.create(user));
    }

    private void validateToCreate(User user) {
        String email = user.getEmail();
        if (email == null) {
            log.info("У пользователя {} не задана электронная почта", user);
            throw new ValidationException();
        }
        if (!email.contains("@")) {
            log.info("У пользователя {} указана неверная электронная почта", user);
            throw new ValidationException();
        }
        List<User> users = getAll().stream().map(userMapper::toUser).collect(Collectors.toList());
        for (User userToCheck : users) {
            if (userToCheck.getEmail().equals(email)) {
                log.info("В системе уже есть пользователь c id {} с такой же почтой {}",
                        userToCheck.getId(),
                        userToCheck.getEmail());
                throw new ConflictException();
            }
        }
    }

    public UserDto update(long userId, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validateToUpdate(userId, user);
        return userMapper.toUserDto(userStorage.update(userId, user));
    }

    private void validateToUpdate(long userId, User user) {
        String email = user.getEmail();
        if (email != null && !email.contains("@")) {
            log.info("У пользователя {} указана неверная электронная почта", user);
            throw new ValidationException();
        }
        List<User> users = getAll().stream().map(userMapper::toUser).collect(Collectors.toList());
        for (User userToCheck : users) {
            if (userToCheck.getId() != userId && userToCheck.getEmail().equals(email)) {
                log.info("В системе уже есть пользователь c id {} с такой же почтой {}",
                        userToCheck.getId(),
                        userToCheck.getEmail());
                throw new ConflictException();
            }
        }
    }

    public UserDto getUser(long userId) {
        return userMapper.toUserDto(userStorage.getUser(userId));
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
