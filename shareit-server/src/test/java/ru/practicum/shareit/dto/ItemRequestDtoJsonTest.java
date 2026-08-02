package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemRequestDto_shouldReturnJson() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.of(2026, 8, 1, 10, 0));

        ItemRequestDto.ItemResponseDto item = new ItemRequestDto.ItemResponseDto();
        item.setId(1L);
        item.setName("Drill");
        item.setOwnerId(2L);
        requestDto.setItems(List.of(item));

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"items\"");
    }

    @Test
    void deserializeItemRequestDto_shouldReturnObject() throws Exception {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2026-08-01T10:00:00\",\"items\":[]}";

        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getItems()).isEmpty();
    }
}