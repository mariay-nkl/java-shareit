package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BaseClient baseClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createItem_withValidData_shouldReturnOk() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Test Item");
        createDto.setDescription("Test Description");
        createDto.setAvailable(true);

        when(baseClient.post(eq("/items"), any(), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_withEmptyName_shouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("");
        createDto.setDescription("Test Description");
        createDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withEmptyDescription_shouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Test Item");
        createDto.setDescription("");
        createDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_withoutAvailable_shouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Test Item");
        createDto.setDescription("Test Description");

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItem_shouldReturnOk() throws Exception {
        when(baseClient.get(eq("/items/1"), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwner_shouldReturnOk() throws Exception {
        when(baseClient.get(eq("/items"), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_shouldAllowPartialUpdate() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setAvailable(false);

        when(baseClient.patch(eq("/items/1"), any(), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_withAllFields_shouldReturnOk() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        when(baseClient.patch(eq("/items/1"), any(), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturnOk() throws Exception {
        when(baseClient.get(eq("/items/search?text=drill")))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_withEmptyText_shouldReturnOk() throws Exception {
        when(baseClient.get(eq("/items/search?text=")))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_withValidText_shouldReturnOk() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        when(baseClient.post(eq("/items/1/comment"), any(), eq(1L)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_withEmptyText_shouldReturnBadRequest() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }
}