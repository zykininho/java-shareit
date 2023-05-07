package ru.practicum.shareit.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStateTest {

    @Test
    void testGetTextValue() {

        assertAll(
                () -> assertEquals("ALL", BookingState.ALL.name()),
                () -> assertEquals("CURRENT", BookingState.CURRENT.name()),
                () -> assertEquals("PAST", BookingState.PAST.name()),
                () -> assertEquals("FUTURE", BookingState.FUTURE.name()),
                () -> assertEquals("WAITING", BookingState.WAITING.name()),
                () -> assertEquals("REJECTED", BookingState.REJECTED.name())
        );

    }

}