package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestTest {

    private static ItemRequest originRequest;
    private static ItemRequest copyRequest;
    private static ItemRequest anotherRequest;

    @BeforeAll
    static void beforeAll() {
        User user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();

        originRequest = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .created(LocalDateTime.of(2023, 5, 1, 12, 0, 0))
                .requestor(user)
                .build();

        copyRequest = new ItemRequest();
        copyRequest.setId(originRequest.getId());
        copyRequest.setDescription(originRequest.getDescription());
        copyRequest.setCreated(originRequest.getCreated());
        copyRequest.setRequestor(originRequest.getRequestor());

        anotherRequest = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .created(LocalDateTime.of(2023, 5, 1, 15, 0, 0))
                .requestor(user)
                .build();
    }

    @Test
    void compareEqualRequests() {
        boolean equalId = originRequest.getId().equals(copyRequest.getId());
        assertTrue(equalId);
        boolean equalDescription = originRequest.getDescription().equals(copyRequest.getDescription());
        assertTrue(equalDescription);
        boolean equalCreated = originRequest.getCreated().equals(copyRequest.getCreated());
        assertTrue(equalCreated);
        boolean equalRequestor = originRequest.getRequestor().equals(copyRequest.getRequestor());
        assertTrue(equalRequestor);
        boolean equal = originRequest.equals(copyRequest);
        assertTrue(equal);
    }

    @Test
    void compareDifferentUsers() {
        boolean equalId = originRequest.getId().equals(anotherRequest.getId());
        assertFalse(equalId);
        boolean equalDescription = originRequest.getDescription().equals(anotherRequest.getDescription());
        assertFalse(equalDescription);
        boolean equalCreated = originRequest.getCreated().equals(anotherRequest.getCreated());
        assertFalse(equalCreated);
        boolean equalRequestor = originRequest.getRequestor().equals(anotherRequest.getRequestor());
        assertTrue(equalRequestor);
        boolean equal = originRequest.equals(anotherRequest);
        assertFalse(equal);

        equalId = copyRequest.getId().equals(anotherRequest.getId());
        assertFalse(equalId);
        equalDescription = copyRequest.getDescription().equals(anotherRequest.getDescription());
        assertFalse(equalDescription);
        equalCreated = copyRequest.getCreated().equals(anotherRequest.getCreated());
        assertFalse(equalCreated);
        equalRequestor = copyRequest.getRequestor().equals(anotherRequest.getRequestor());
        assertTrue(equalRequestor);
        equal = copyRequest.equals(anotherRequest);
        assertFalse(equal);
    }

}