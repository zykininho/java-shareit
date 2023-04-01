package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class UserDto {

    long id;
    String name;
    String description;
    boolean available;
    User owner;
    long requestId;

    public UserDto(String name, String description, boolean available, long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
