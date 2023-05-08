package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public ItemRequestDto create(long userId, ItemRequestShortDto itemRequestShortDto) {
        User owner = findUser(userId);
        validate(itemRequestShortDto);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestShortDto.getDescription())
                .created(LocalDateTime.now())
                .requestor(owner)
                .build();
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Создан новый запрос {} от пользователя с id={}", savedItemRequest, owner.getId());
        return itemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        User owner = findUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(owner.getId(),
                getSortByCreatedForItemRequests());
        log.info("Найдено {} запросов от пользователя с id={}", itemRequests.size(), owner.getId());
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestsDto.forEach(this::addItems);
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        User owner = findUser(userId);
        if (requestId == 0) {
            log.info("Указан неверный id={} запроса от пользователя с id={}", requestId, owner.getId());
            throw new ValidationException();
        }
        ItemRequest itemRequest = findById(requestId);
        log.info("Найден запрос с id={} от пользователя с id={}", requestId, owner.getId());
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        addItems(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> search(long userId, Integer from, Integer size) {
        User owner = findUser(userId);
        List<ItemRequest> itemRequests;
        if (from != null && size != null) {
            validateSearchParameters(from, size);
            itemRequests = itemRequestRepository.findAllByOtherRequestors(owner.getId(),
                    PageRequest.of(from / size, size, getSortByCreatedForItemRequests()));
        } else {
            itemRequests = itemRequestRepository.findAllByOtherRequestors(owner.getId(),
                    getSortByCreatedForItemRequests());
        }
        log.info("Найдено {} других запросов для пользователя с id={}", itemRequests.size(), owner.getId());
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestsDto.forEach(this::addItems);
        return itemRequestsDto;
    }

    private User findUser(long userId) {
        if (userId == 0) {
            throw new ValidationException();
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        return user.get();
    }

    public ItemRequest findById(long requestId) {
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty()) {
            log.info("Не найден запрос с id={}", requestId);
            throw new NotFoundException();
        }
        ItemRequest itemRequest = optionalItemRequest.get();
        log.info("Найден запрос с id={}", requestId);
        return itemRequest;
    }

    private void validate(ItemRequestShortDto itemRequestShortDto) {
        if (itemRequestShortDto.getDescription() == null || itemRequestShortDto.getDescription().isBlank()) {
            log.info("В запросе {} не указано описание", itemRequestShortDto);
            throw new ValidationException();
        }
    }

    private void validateSearchParameters(int from, int size) {
        if (from < 0) {
            log.info("Параметр запроса 'from' должен быть больше или равен 0, указано значение {}", from);
            throw new ValidationException();
        } else if (size <= 0) {
            log.info("Параметр запроса 'size' должен быть больше 0, указано значение {}", size);
            throw new ValidationException();
        }
    }

    private void addItems(ItemRequestDto itemRequestDto) {
        List<Item> requestItems = itemRepository.findAllByRequestId(itemRequestDto.getId());
        List<ItemDto> itemsDto = requestItems.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemsDto);
    }

    private Sort getSortByCreatedForItemRequests() {
        return Sort.by("created").descending();
    }

}