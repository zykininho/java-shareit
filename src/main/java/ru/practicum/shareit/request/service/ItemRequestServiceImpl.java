package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> search(long userId, String from, String size) {
        return null;
    }

}