package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class User {

    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;

}
