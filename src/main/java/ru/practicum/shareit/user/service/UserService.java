package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto getUser(long userId);

    void deleteUser(long userId);

}