package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRepository itemRepository;

    private Long ownerId;
    private Long bookerId;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        UserCreateDto ownerDto = new UserCreateDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@example.com");
        UserDto owner = userService.createUser(ownerDto);
        ownerId = owner.getId();

        UserCreateDto bookerDto = new UserCreateDto();
        bookerDto.setName("Booker");
        bookerDto.setEmail("booker@example.com");
        UserDto booker = userService.createUser(bookerDto);
        bookerId = booker.getId();
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
        assertThat(savedItem.getRequestId()).isNull();
    }

    @Test
    void createItem_withRequestId_shouldSaveItemWithRequest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Need a drill");
        ItemRequestDto savedRequest = requestService.createRequest(ownerId, requestDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful cordless drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(savedRequest.getId());

        ItemDto savedItem = itemService.createItem(ownerId, itemCreateDto);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getRequestId()).isEqualTo(savedRequest.getId());

        List<ru.practicum.shareit.item.model.Item> items = itemRepository.findByRequestId(savedRequest.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(savedItem.getId());
    }

    @Test
    void createItem_withInvalidRequestId_shouldThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful cordless drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(999L);

        assertThrows(RuntimeException.class, () -> {
            itemService.createItem(ownerId, itemCreateDto);
        });
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
        assertThat(foundItem.getComments()).isEmpty();
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
    void updateItem_partialUpdate_shouldUpdateOnlyProvidedFields() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(ownerId, itemCreateDto);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        ItemDto updatedItem = itemService.updateItem(ownerId, savedItem.getId(), updateDto);

        assertThat(updatedItem.getName()).isEqualTo("Updated Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Test Description");
        assertThat(updatedItem.getAvailable()).isTrue();
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

    @Test
    void addComment_withCompletedBooking_shouldSaveComment() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        ItemDto item = itemService.createItem(ownerId, itemCreateDto);

        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto booking = bookingService.createBooking(bookerId, bookingDto);
        bookingService.approveBooking(ownerId, booking.getId(), true);

        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");
        CommentDto savedComment = itemService.addComment(bookerId, item.getId(), commentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Great item!");
        assertThat(savedComment.getAuthorName()).isNotNull();
        assertThat(savedComment.getCreated()).isNotNull();

        ItemDto itemWithComments = itemService.getItem(item.getId(), ownerId);
        assertThat(itemWithComments.getComments()).hasSize(1);
        assertThat(itemWithComments.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void addComment_withoutCompletedBooking_shouldThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        ItemDto item = itemService.createItem(ownerId, itemCreateDto);

        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(bookerId, item.getId(), commentDto));
    }

    @Test
    void addComment_withRejectedBooking_shouldThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        ItemDto item = itemService.createItem(ownerId, itemCreateDto);

        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto booking = bookingService.createBooking(bookerId, bookingDto);
        bookingService.approveBooking(ownerId, booking.getId(), false);

        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(bookerId, item.getId(), commentDto));
    }

    @Test
    void addComment_byOwner_shouldThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        ItemDto item = itemService.createItem(ownerId, itemCreateDto);

        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("My own item!");

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(ownerId, item.getId(), commentDto));
    }
}