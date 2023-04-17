package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingForItemDto toBookingForItemDto(Booking booking);

}