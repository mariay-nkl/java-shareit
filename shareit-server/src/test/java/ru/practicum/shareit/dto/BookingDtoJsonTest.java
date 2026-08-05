package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeBookingDto_shouldReturnJson() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2026, 8, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2026, 8, 2, 10, 0));
        bookingDto.setStatus("WAITING");

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
    }

    @Test
    void deserializeBookingDto_shouldReturnObject() throws Exception {
        String json = "{\"id\":1,\"start\":\"2026-08-01T10:00:00\",\"end\":\"2026-08-02T10:00:00\",\"status\":\"WAITING\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStatus()).isEqualTo("WAITING");
    }
}