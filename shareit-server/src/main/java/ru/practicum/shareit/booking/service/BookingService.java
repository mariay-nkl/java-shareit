package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto);
    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);
    BookingDto getBooking(Long userId, Long bookingId);
    List<BookingDto> getUserBookings(Long userId, BookingState state);
    List<BookingDto> getOwnerBookings(Long userId, BookingState state);
}