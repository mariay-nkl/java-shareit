package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);
    ItemDto getItem(Long id);
    List<ItemDto> getItemsByOwner(Long userId);
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);
    List<ItemDto> searchItems(String text);
}