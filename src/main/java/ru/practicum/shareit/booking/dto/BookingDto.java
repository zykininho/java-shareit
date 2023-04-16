package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Builder
public class BookingDto {

    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;

}