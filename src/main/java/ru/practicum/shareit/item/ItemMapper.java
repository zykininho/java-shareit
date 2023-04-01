package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.UserDto;
import ru.practicum.shareit.item.model.User;

public class ItemMapper {

    public static UserDto toItemDto(User item) {
        return new UserDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static User toItem(UserDto itemDto) {
        return new UserDto(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.isAvailable(),
                //itemDto.getRequestId()
        );
    }

}
