package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestRepository requestRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();

        UserCreateDto userDto = new UserCreateDto();
        userDto.setName("Requester");
        userDto.setEmail("requester@example.com");
        UserDto user = userService.createUser(userDto);
        userId = user.getId();
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
    }

    @Test
    void getUserRequests_shouldReturnRequests() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        requestService.createRequest(userId, createDto);

        List<ItemRequestDto> requests = requestService.getUserRequests(userId);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        requestService.createRequest(userId, createDto);

        UserCreateDto otherUserDto = new UserCreateDto();
        otherUserDto.setName("Other");
        otherUserDto.setEmail("other@example.com");
        UserDto otherUser = userService.createUser(otherUserDto);

        List<ItemRequestDto> requests = requestService.getAllRequests(otherUser.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getRequest_shouldReturnRequestById() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
        ItemRequestDto savedRequest = requestService.createRequest(userId, createDto);

        ItemRequestDto foundRequest = requestService.getRequest(userId, savedRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(savedRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("Need a drill");
    }
}