package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static User user;
    private static User copyUser;
    private static User anotherUser;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();

        copyUser = new User();
        copyUser.setId(user.getId());
        copyUser.setName(user.getName());
        copyUser.setEmail(user.getEmail());

        anotherUser = User.builder()
                .id(2L)
                .name("anotherUserName")
                .email("another_user@ya.ru")
                .build();
    }

    @Test
    void compareEqualUsers() {
        boolean equalId = user.getId().equals(copyUser.getId());
        assertTrue(equalId);
        boolean equalName = user.getName().equals(copyUser.getName());
        assertTrue(equalName);
        boolean equalEmail = user.getEmail().equals(copyUser.getEmail());
        assertTrue(equalEmail);
        boolean equal = user.equals(copyUser);
        assertTrue(equal);
    }

    @Test
    void compareDifferentUsers() {
        boolean equalId = user.getId().equals(anotherUser.getId());
        assertFalse(equalId);
        boolean equalName = user.getName().equals(anotherUser.getName());
        assertFalse(equalName);
        boolean equalEmail = user.getEmail().equals(anotherUser.getEmail());
        assertFalse(equalEmail);
        boolean equal = user.equals(anotherUser);
        assertFalse(equal);

        equalId = copyUser.getId().equals(anotherUser.getId());
        assertFalse(equalId);
        equalName = copyUser.getName().equals(anotherUser.getName());
        assertFalse(equalName);
        equalEmail = copyUser.getEmail().equals(anotherUser.getEmail());
        assertFalse(equalEmail);
        equal = copyUser.equals(anotherUser);
        assertFalse(equal);
    }

}