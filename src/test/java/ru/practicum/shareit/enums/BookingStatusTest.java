package ru.practicum.shareit.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStatusTest {

    @Test
    void testGetTextValue() {

        assertAll(
                () -> assertEquals("WAITING", BookingStatus.WAITING.name()),
                () -> assertEquals("APPROVED", BookingStatus.APPROVED.name()),
                () -> assertEquals("REJECTED", BookingStatus.REJECTED.name()),
                () -> assertEquals("CANCELED", BookingStatus.CANCELED.name())
        );

    }

}