package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    private static User user;
    private static UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();
        userDto = userMapper.toUserDto(user);
    }

    @Test
    void createUserDtoFromUser() {
        UserDto userDtoFromMapper = UserMapper.INSTANCE.toUserDto(user);
        assertEquals(userDto, userDtoFromMapper);
    }

    @Test
    void createUserFromUserDto() {
        User userFromMapper = UserMapper.INSTANCE.toUser(userDto);
        assertEquals(user, userFromMapper);
    }

}