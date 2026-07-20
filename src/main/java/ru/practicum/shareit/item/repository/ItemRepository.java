package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    public Item update(Item item) {
        Item existingItem = findById(item.getId());

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        items.put(existingItem.getId(), existingItem);
        return existingItem;
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item ->
                        item.getName().toLowerCase().contains(lowerText) ||
                                item.getDescription().toLowerCase().contains(lowerText)
                )
                .collect(Collectors.toList());
    }
}