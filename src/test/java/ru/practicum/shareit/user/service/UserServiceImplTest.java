package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    private static User user;
    private static UserDto userDto;
    private static User anotherUser;
    private static UserDto anotherUserDto;
    private static List<User> users;
    private static List<UserDto> usersDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();
        userDto = userMapper.toUserDto(user);

        anotherUser = User.builder()
                .id(2L)
                .name("anotherUserName")
                .email("another_user@ya.ru")
                .build();
        anotherUserDto = userMapper.toUserDto(anotherUser);

        users = List.of(user, anotherUser);
        usersDto = users.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Test
    void saveNewUsers() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userMapper.toUser(userService.create(userDto));
        assertEquals(user, savedUser);

        when(userRepository.save(anotherUser)).thenReturn(anotherUser);
        User savedAnotherUser = userMapper.toUser(userService.create(anotherUserDto));
        assertEquals(anotherUser, savedAnotherUser);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(users);
        userService.create(userDto);
        userService.create(anotherUserDto);
        List<UserDto> savedUsersDto = userService.getAll();
        assertEquals(usersDto, savedUsersDto);
        List<User> savedUsers = savedUsersDto.stream().map(userMapper::toUser).collect(Collectors.toList());
        assertEquals(users, savedUsers);
    }

    @Test
    void saveUserAndUpdate() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userMapper.toUser(userService.create(userDto));
        assertEquals(user, savedUser);

        when(userRepository.save(anotherUser)).thenReturn(anotherUser);
        User savedAnotherUser = userMapper.toUser(userService.create(anotherUserDto));
        assertEquals(anotherUser, savedAnotherUser);

        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> savedUsersDto = userService.getAll();
        assertEquals(usersDto, savedUsersDto);

        List<User> savedUsers = savedUsersDto.stream().map(userMapper::toUser).collect(Collectors.toList());
        assertEquals(users, savedUsers);

        user.setName("New user name");
        user.setEmail("new_user@ya.ru");
        userDto = userMapper.toUserDto(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);
        UserDto updatedUserDto = userService.update(userDto.getId(), userDto);
        assertEquals(userDto, updatedUserDto);
        User updatedUser = userMapper.toUser(updatedUserDto);
        assertEquals(user, updatedUser);
    }

    @Test
    void saveUserAndGet() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userMapper.toUser(userService.create(userDto));
        assertEquals(user, savedUser);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        UserDto savedUsersDto = userService.getUser(user.getId());
        assertEquals(userDto, savedUsersDto);
        User foundUser = userMapper.toUser(savedUsersDto);
        assertEquals(user, foundUser);
    }

    @Test
    void saveUserAndDelete() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userMapper.toUser(userService.create(userDto));
        assertEquals(user, savedUser);

        doNothing().when(userRepository).deleteById(anyLong());
    }

}