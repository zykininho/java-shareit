package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemMapperTest {

    @Autowired
    ItemMapper itemMapper;

    private static Item item;
    private static ItemDto itemDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(false)
                .owner(user)
                .build();
        itemDto = itemMapper.toItemDto(item);
    }

    @Test
    void createItemDtoFromItem() {
        ItemDto itemDtoFromMapper = ItemMapper.INSTANCE.toItemDto(item);
        assertEquals(itemDto, itemDtoFromMapper);
    }

    @Test
    void createItemFromItemDto() {
        Item itemFromMapper = ItemMapper.INSTANCE.toItem(itemDto);
        assertEquals(item, itemFromMapper);
    }

}