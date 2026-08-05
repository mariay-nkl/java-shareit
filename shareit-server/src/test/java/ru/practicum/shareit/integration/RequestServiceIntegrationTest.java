package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRequestRepository requestRepository;

    private Long userId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();

        UserCreateDto userDto = new UserCreateDto();
        userDto.setName("Requester");
        userDto.setEmail("requester@example.com");
        UserDto user = userService.createUser(userDto);
        userId = user.getId();

        UserCreateDto otherUserDto = new UserCreateDto();
        otherUserDto.setName("Other");
        otherUserDto.setEmail("other@example.com");
        UserDto otherUser = userService.createUser(otherUserDto);
        otherUserId = otherUser.getId();
    }

    @Test
    void createRequest_shouldSaveRequestToDatabase() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Need a drill");
        assertThat(savedRequest.getCreated()).isNotNull();
        assertThat(savedRequest.getItems()).isEmpty();
    }

    @Test
    void createRequest_withEmptyDescription_shouldSaveRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("");

        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEmpty();
        assertThat(savedRequest.getCreated()).isNotNull();
    }

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(savedRequest.getId());
        itemService.createItem(userId, itemCreateDto);

        List<ItemRequestDto> requests = requestService.getUserRequests(userId);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
        assertThat(requests.get(0).getItems()).hasSize(1);
        assertThat(requests.get(0).getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void getUserRequests_shouldReturnRequestsSortedByCreatedDesc() {
        ItemRequestCreateDto createDto1 = new ItemRequestCreateDto();
        createDto1.setDescription("First request");
        requestService.createRequest(userId, createDto1);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ItemRequestCreateDto createDto2 = new ItemRequestCreateDto();
        createDto2.setDescription("Second request");
        requestService.createRequest(userId, createDto2);

        List<ItemRequestDto> requests = requestService.getUserRequests(userId);

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Second request");
        assertThat(requests.get(1).getDescription()).isEqualTo("First request");
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        requestService.createRequest(userId, createDto);

        List<ItemRequestDto> requests = requestService.getAllRequests(otherUserId);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getAllRequests_shouldNotReturnOwnRequests() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("My request");
        requestService.createRequest(userId, createDto);

        List<ItemRequestDto> requests = requestService.getAllRequests(userId);

        assertThat(requests).isEmpty();
    }

    @Test
    void getAllRequests_shouldReturnRequestsSortedByCreatedDesc() {
        ItemRequestCreateDto createDto1 = new ItemRequestCreateDto();
        createDto1.setDescription("First request");
        requestService.createRequest(userId, createDto1);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ItemRequestCreateDto createDto2 = new ItemRequestCreateDto();
        createDto2.setDescription("Second request");
        requestService.createRequest(otherUserId, createDto2);

        List<ItemRequestDto> requests = requestService.getAllRequests(userId);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Second request");
    }

    @Test
    void getRequest_shouldReturnRequestById() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Drill");
        itemCreateDto.setDescription("Powerful drill");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(savedRequest.getId());
        itemService.createItem(userId, itemCreateDto);

        ItemRequestDto foundRequest = requestService.getRequest(userId, savedRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(savedRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("Need a drill");
        assertThat(foundRequest.getItems()).hasSize(1);
        assertThat(foundRequest.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void getRequest_withInvalidRequestId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            requestService.getRequest(userId, 999L);
        });
    }

    @Test
    void getRequest_byOtherUser_shouldReturnRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        ItemRequestDto foundRequest = requestService.getRequest(otherUserId, savedRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getUserRequests_withInvalidUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            requestService.getUserRequests(999L);
        });
    }
}