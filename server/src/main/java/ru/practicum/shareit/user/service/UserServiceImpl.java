package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validateToCreate(user);
        user = userRepository.save(user);
        log.info("Добавлен новый пользователь: {}", user);
        return userMapper.toUserDto(user);
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
    }

    public UserDto update(long userId, UserDto userDto) {
        User user = findUser(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        validateToUpdate(userId, user);
        return userMapper.toUserDto(userRepository.save(user));
    }

    private User findUser(long userId) {
        if (userId == 0) {
            throw new ValidationException();
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return user.get();
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
        if (userId == 0) {
            throw new ValidationException();
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return userMapper.toUserDto(user.get());
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

}