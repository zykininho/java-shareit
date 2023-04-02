package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
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

    public List<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateToCreate(user);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    private void validateToCreate(User user) {
        String email = user.getEmail();
        List<UserDto> usersDto = getAll();
        List<User> users = usersDto.stream().map(UserMapper::toUser).collect(Collectors.toList());
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

    public UserDto update(long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateToUpdate(userId, user);
        return UserMapper.toUserDto(userStorage.update(userId, user));
    }

    private void validateToUpdate(long userId, User user) {
        String email = user.getEmail();
        if (email != null && !email.contains("@")) {
            log.info("У пользователя указана неверная электронная почта");
            throw new ValidationException();
        }
        List<UserDto> usersDto = getAll();
        List<User> users = usersDto.stream().map(UserMapper::toUser).collect(Collectors.toList());
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
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
