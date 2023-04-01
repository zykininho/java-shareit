package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.User;

import java.time.LocalDate;

@Data
@Builder
public class Booking {

    long id;
    LocalDate start;
    LocalDate end;
    User item;
    ru.practicum.shareit.user.model.User booker;
    Status status;

}
