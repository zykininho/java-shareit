package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.enums.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

	@Autowired
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Received GET-request at /bookings?state={}&from={}&size={} endpoint from user id={}",
				state, from, size, userId);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid BookingFromUserDto bookingFromUser) {
		log.info("Received POST-request at /bookings endpoint from user id={}", userId);
		return bookingClient.bookItem(userId, bookingFromUser);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Received GET-request at /bookings/{} endpoint from user id={}", userId, bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getItemsOwnerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
														@RequestParam(defaultValue = "all") String stateParam,
														@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
														@Positive @RequestParam(defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Received GET-request at /bookings/owner?state={}&from={}&size={} endpoint from user id={}",
				state, from, size, userId);
		return bookingClient.getItemsOwnerBookings(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") long userId,
											 @PathVariable long bookingId,
											 @RequestParam String approved) {
		log.info("Received PATCH-request at /bookings/{} endpoint from user id={}", bookingId, userId);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

}