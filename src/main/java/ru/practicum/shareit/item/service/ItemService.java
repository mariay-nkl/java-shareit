package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemCreateDto itemCreateDto);
    ItemDto getItem(Long id, Long userId);
    List<ItemDto> getItemsByOwner(Long userId);
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);
    List<ItemDto> searchItems(String text);
    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}