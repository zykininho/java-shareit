package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {

    long id;
    String description;
    User requestor;
    LocalDateTime created;

}
