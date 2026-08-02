package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");
    }

    @Test
    void createUser_shouldSaveUserToDatabase() {
        UserDto savedUser = userService.createUser(userCreateDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUser_shouldReturnUserFromDatabase() {
        UserDto savedUser = userService.createUser(userCreateDto);
        UserDto foundUser = userService.getUser(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getName()).isEqualTo(savedUser.getName());
        assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        userService.createUser(userCreateDto);

        UserCreateDto secondUser = new UserCreateDto();
        secondUser.setName("Second User");
        secondUser.setEmail("second@example.com");
        userService.createUser(secondUser);

        var users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void updateUser_shouldUpdateUserInDatabase() {
        UserDto savedUser = userService.createUser(userCreateDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void deleteUser_shouldRemoveUserFromDatabase() {
        UserDto savedUser = userService.createUser(userCreateDto);

        userService.deleteUser(savedUser.getId());

        assertThrows(RuntimeException.class, () -> userService.getUser(savedUser.getId()));
    }
}