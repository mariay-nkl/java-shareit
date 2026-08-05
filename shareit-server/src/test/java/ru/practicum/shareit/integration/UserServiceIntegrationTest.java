package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
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
    void createUser_withDuplicateEmail_shouldThrowException() {
        userService.createUser(userCreateDto);

        UserCreateDto duplicateDto = new UserCreateDto();
        duplicateDto.setName("Another User");
        duplicateDto.setEmail("test@example.com");

        assertThrows(ConflictException.class, () -> {
            userService.createUser(duplicateDto);
        });
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
    void getUser_withInvalidId_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUser(999L);
        });
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
        assertThat(users).extracting(UserDto::getName)
                .containsExactlyInAnyOrder("Test User", "Second User");
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
    void updateUser_partialUpdate_shouldUpdateOnlyProvidedFields() {
        UserDto savedUser = userService.createUser(userCreateDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowException() {
        UserDto savedUser = userService.createUser(userCreateDto);

        UserCreateDto secondUserDto = new UserCreateDto();
        secondUserDto.setName("Second User");
        secondUserDto.setEmail("second@example.com");
        UserDto secondUser = userService.createUser(secondUserDto);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("test@example.com");

        assertThrows(ConflictException.class, () -> {
            userService.updateUser(secondUser.getId(), updateDto);
        });
    }

    @Test
    void updateUser_withInvalidId_shouldThrowException() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, updateDto);
        });
    }

    @Test
    void deleteUser_shouldRemoveUserFromDatabase() {
        UserDto savedUser = userService.createUser(userCreateDto);

        userService.deleteUser(savedUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(savedUser.getId()));
    }

    @Test
    void deleteUser_withInvalidId_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
}