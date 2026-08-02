package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.constant.Headers;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BaseClient client;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(Headers.USER_ID) Long userId,
            @Valid @RequestBody BookingCreateDto bookingDto) {
        return client.post("/bookings", bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved) {
        return client.patch("/bookings/" + bookingId + "?approved=" + approved, null, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return client.get("/bookings/" + bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return client.get("/bookings?state=" + state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(Headers.USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return client.get("/bookings/owner?state=" + state, userId);
    }
}