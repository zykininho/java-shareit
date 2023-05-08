package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private long id;
    @NotNull
    private String text;
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;

}