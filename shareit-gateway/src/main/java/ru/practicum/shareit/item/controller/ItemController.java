package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final BaseClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(Headers.USER_ID) Long userId,
            @Valid @RequestBody ItemCreateDto itemDto) {
        return client.post("/items", itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable("itemId") Long itemId,
            @RequestHeader(Headers.USER_ID) Long userId) {
        return client.get("/items/" + itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(Headers.USER_ID) Long userId) {
        return client.get("/items", userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemUpdateDto itemDto) {
        return client.patch("/items/" + itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        return client.get("/items/search?text=" + text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(Headers.USER_ID) Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto) {
        return client.post("/items/" + itemId + "/comment", commentDto, userId);
    }
}