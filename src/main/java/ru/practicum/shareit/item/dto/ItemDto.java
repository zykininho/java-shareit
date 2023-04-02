package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemDto {

    long id;
    String name;
    String description;
    Boolean available;
    User owner;
    long request;

}
