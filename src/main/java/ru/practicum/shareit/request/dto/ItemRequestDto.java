package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {

    long id;
    String description;
    User requestor;
    LocalDateTime created;

}
