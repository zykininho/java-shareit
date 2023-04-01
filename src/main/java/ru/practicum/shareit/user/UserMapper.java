package ru.practicum.shareit.user;

import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getDescription(),
                user.isAvailable(),
                user.getRequest() != null ? user.getRequest().getId() : null
        );
    }

    public static User toUser(UserDto userDto) {
        return new UserDto(
                userDto.getName(),
                userDto.getDescription(),
                userDto.isAvailable(),
                //userDto.getRequestId()
        );
    }

}
