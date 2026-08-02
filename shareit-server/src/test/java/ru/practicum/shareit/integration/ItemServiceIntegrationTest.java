package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Owner");
        userCreateDto.setEmail("owner@example.com");
        UserDto owner = userService.createUser(userCreateDto);
        ownerId = owner.getId();
    }

    @Test
    void createItem_shouldSaveItemToDatabase() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(ownerId, itemCreateDto);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getDescription()).isEqualTo("Test Description");
        assertThat(savedItem.getAvailable()).isTrue();
    }

    @Test
    void getItem_shouldReturnItemFromDatabase() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(ownerId, itemCreateDto);
        ItemDto foundItem = itemService.getItem(savedItem.getId(), ownerId);

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(savedItem.getId());
    }

    @Test
    void getItemsByOwner_shouldReturnAllItems() {
        ItemCreateDto itemCreateDto1 = new ItemCreateDto();
        itemCreateDto1.setName("Item 1");
        itemCreateDto1.setDescription("Description 1");
        itemCreateDto1.setAvailable(true);

        ItemCreateDto itemCreateDto2 = new ItemCreateDto();
        itemCreateDto2.setName("Item 2");
        itemCreateDto2.setDescription("Description 2");
        itemCreateDto2.setAvailable(true);

        itemService.createItem(ownerId, itemCreateDto1);
        itemService.createItem(ownerId, itemCreateDto2);

        List<ItemDto> items = itemService.getItemsByOwner(ownerId);

        assertThat(items).hasSize(2);
    }

    @Test
    void updateItem_shouldUpdateItemInDatabase() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(ownerId, itemCreateDto);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(ownerId, savedItem.getId(), updateDto);

        assertThat(updatedItem.getName()).isEqualTo("Updated Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Search Me");
        itemCreateDto.setDescription("This is searchable");
        itemCreateDto.setAvailable(true);

        itemService.createItem(ownerId, itemCreateDto);

        List<ItemDto> results = itemService.searchItems("search");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).isEqualTo("Search Me");
    }

    @Test
    void searchItems_shouldReturnEmptyWhenNoMatch() {
        List<ItemDto> results = itemService.searchItems("nonexistent");
        assertThat(results).isEmpty();
    }
}