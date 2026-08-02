package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
    void getBooking_shouldReturnBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(itemId);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.createBooking(bookerId, createDto);
        BookingDto foundBooking = bookingService.getBooking(bookerId, savedBooking.getId());

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(savedBooking.getId());
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
    }
}