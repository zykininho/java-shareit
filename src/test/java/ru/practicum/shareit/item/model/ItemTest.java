package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    private static Item originItem;
    private static Item copyItem;
    private static Item anotherItem;

    @BeforeAll
    static void beforeAll() {
        User user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();

        originItem = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(false)
                .owner(user)
                .build();

        copyItem = new Item();
        copyItem.setId(originItem.getId());
        copyItem.setName(originItem.getName());
        copyItem.setDescription(originItem.getDescription());
        copyItem.setAvailable(originItem.getAvailable());
        copyItem.setOwner(originItem.getOwner());

        anotherItem = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description of item 2")
                .available(false)
                .owner(user)
                .build();
    }

    @Test
    void compareEqualItems() {
        boolean equalId = originItem.getId().equals(copyItem.getId());
        assertTrue(equalId);
        boolean equalName = originItem.getName().equals(copyItem.getName());
        assertTrue(equalName);
        boolean equalDescription = originItem.getDescription().equals(copyItem.getDescription());
        assertTrue(equalDescription);
        boolean equalAvailable = originItem.getAvailable().equals(copyItem.getAvailable());
        assertTrue(equalAvailable);
        boolean equalOwner = originItem.getOwner().equals(copyItem.getOwner());
        assertTrue(equalOwner);
        boolean equal = originItem.equals(copyItem);
        assertTrue(equal);
    }

    @Test
    void compareDifferentItems() {
        boolean equalId = originItem.getId().equals(anotherItem.getId());
        assertFalse(equalId);
        boolean equalName = originItem.getName().equals(anotherItem.getName());
        assertFalse(equalName);
        boolean equalDescription = originItem.getDescription().equals(anotherItem.getDescription());
        assertFalse(equalDescription);
        boolean equalAvailable = originItem.getAvailable().equals(anotherItem.getAvailable());
        assertTrue(equalAvailable);
        boolean equalOwner = originItem.getOwner().equals(anotherItem.getOwner());
        assertTrue(equalOwner);
        boolean equal = originItem.equals(anotherItem);
        assertFalse(equal);

        equalId = copyItem.getId().equals(anotherItem.getId());
        assertFalse(equalId);
        equalName = copyItem.getName().equals(anotherItem.getName());
        assertFalse(equalName);
        equalDescription = copyItem.getDescription().equals(anotherItem.getDescription());
        assertFalse(equalDescription);
        equalAvailable = copyItem.getAvailable().equals(anotherItem.getAvailable());
        assertTrue(equalAvailable);
        equalOwner = copyItem.getOwner().equals(anotherItem.getOwner());
        assertTrue(equalOwner);
        equal = copyItem.equals(anotherItem);
        assertFalse(equal);
    }

}