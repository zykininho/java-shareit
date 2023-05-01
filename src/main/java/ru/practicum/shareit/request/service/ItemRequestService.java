package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAll(long userId);

    ItemRequestDto getItemRequest(long userId, long requestId);

    List<ItemRequestDto> search(long userId, String from, String size);

}