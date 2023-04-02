package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {

    long id;
    String name;
    String description;
    Boolean available;
    User owner;
    long request;

}
