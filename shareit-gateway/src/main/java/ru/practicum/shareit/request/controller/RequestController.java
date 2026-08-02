package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final BaseClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(Headers.USER_ID) Long userId,
            @Valid @RequestBody ItemRequestCreateDto requestDto) {
        return client.post("/requests", requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(Headers.USER_ID) Long userId) {
        return client.get("/requests", userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(Headers.USER_ID) Long userId) {
        return client.get("/requests/all", userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable("requestId") Long requestId) {
        return client.get("/requests/" + requestId, userId);
    }
}