package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
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
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

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

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        itemId = itemService.createItem(ownerId, itemCreateDto).getId();
    }

    @Test
    void createBooking_shouldSaveBookingToDatabase() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo("WAITING");
        assertThat(savedBooking.getBooker().getId()).isEqualTo(bookerId);
        assertThat(savedBooking.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void createBooking_withUnavailableItem_shouldThrowException() {
        ItemCreateDto unavailableItem = new ItemCreateDto();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Not available");
        unavailableItem.setAvailable(false);
        Long unavailableItemId = itemService.createItem(ownerId, unavailableItem).getId();

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(unavailableItemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookerId, createDto);
        });
    }

    @Test
    void createBooking_byOwner_shouldThrowException() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ForbiddenException.class, () -> {
            bookingService.createBooking(ownerId, createDto);
        });
    }

    @Test
    void createBooking_withInvalidItem_shouldThrowException() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(999L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookerId, createDto);
        });
    }

    @Test
    void createBooking_withStartAfterEnd_shouldThrowException() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(2));
        createDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookerId, createDto);
        });
    }

    @Test
    void approveBooking_shouldApproveBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        BookingDto approvedBooking = bookingService.approveBooking(ownerId, savedBooking.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void approveBooking_shouldRejectBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        BookingDto rejectedBooking = bookingService.approveBooking(ownerId, savedBooking.getId(), false);

        assertThat(rejectedBooking.getStatus()).isEqualTo("REJECTED");
    }

    @Test
    void approveBooking_byNonOwner_shouldThrowException() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);

        assertThrows(ForbiddenException.class, () -> {
            bookingService.approveBooking(bookerId, savedBooking.getId(), true);
        });
    }

    @Test
    void approveBooking_alreadyApproved_shouldThrowException() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        bookingService.approveBooking(ownerId, savedBooking.getId(), true);

        assertThrows(ValidationException.class, () -> {
            bookingService.approveBooking(ownerId, savedBooking.getId(), true);
        });
    }

    @Test
    void getBooking_shouldReturnBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        BookingDto foundBooking = bookingService.getBooking(bookerId, savedBooking.getId());

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(savedBooking.getId());
        assertThat(foundBooking.getStatus()).isEqualTo("WAITING");
    }

    @Test
    void getBooking_byOwner_shouldReturnBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        BookingDto foundBooking = bookingService.getBooking(ownerId, savedBooking.getId());

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(savedBooking.getId());
    }

    @Test
    void getBooking_byOtherUser_shouldThrowException() {
        UserCreateDto otherDto = new UserCreateDto();
        otherDto.setName("Other");
        otherDto.setEmail("other@example.com");
        UserDto other = userService.createUser(otherDto);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);

        assertThrows(ForbiddenException.class, () -> {
            bookingService.getBooking(other.getId(), savedBooking.getId());
        });
    }

    @Test
    void getUserBookings_shouldReturnBookings() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(bookerId, createDto);

        List<BookingDto> bookings = bookingService.getUserBookings(bookerId, BookingState.ALL);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getUserBookings_withStateWaiting_shouldReturnWaiting() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(bookerId, createDto);

        List<BookingDto> bookings = bookingService.getUserBookings(bookerId, BookingState.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo("WAITING");
    }

    @Test
    void getUserBookings_shouldReturnEmptyForNoBookings() {
        List<BookingDto> bookings = bookingService.getUserBookings(bookerId, BookingState.ALL);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getOwnerBookings_shouldReturnBookingsForOwnerItems() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(bookerId, createDto);

        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, BookingState.ALL);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void getOwnerBookings_shouldReturnEmptyForNoBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, BookingState.ALL);
        assertThat(bookings).isEmpty();
    }
}