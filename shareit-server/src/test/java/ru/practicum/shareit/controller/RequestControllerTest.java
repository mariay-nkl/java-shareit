package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createRequest_shouldReturnRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Need a drill");
        responseDto.setCreated(LocalDateTime.now());

        when(requestService.createRequest(eq(1L), any(ItemRequestCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getUserRequests_shouldReturnList() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Need a drill");

        when(requestService.getUserRequests(eq(1L))).thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Need a drill");

        when(requestService.getAllRequests(eq(1L))).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequest_shouldReturnRequest() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Need a drill");

        when(requestService.getRequest(eq(1L), eq(1L))).thenReturn(request);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }
}