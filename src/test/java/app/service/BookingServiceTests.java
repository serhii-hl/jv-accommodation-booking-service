package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.exception.BookingUnavailableException;
import app.mapper.BookingMapper;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.model.Booking;
import app.model.BookingStatus;
import app.model.User;
import app.repository.AccommodationRepository;
import app.repository.AccommodationUnitRepository;
import app.repository.BookingRepository;
import app.service.impl.BookingServiceImpl;
import app.util.BookingUtils;
import app.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationUnitRepository accommodationUnitRepository;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @Test
    @DisplayName("get booking by id returns correct object")
    void getBookingByIdSuccess() {
        Booking booking = BookingUtils.createExpectedBooking();
        BookingDto expected = BookingUtils.createExpectedBookingDto();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);
        BookingDto actual = bookingService.getBookingById(100L);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("get booking by wrong id throws exception")
    void getBookingByIdFailure() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingById(99L),
                "Expected deleteAccommodationById to throw EntityNotFoundException, "
                        + "but it didn't"
        );
        assertThat(thrown.getMessage()).isEqualTo("Booking with id: 99 not found.");
    }

    @Test
    @DisplayName("get all user`s bookings returns correct page")
    void getAllUserBookingsSuccess() {
        User user = UserUtils.createExpectedTestUserOwner();
        Pageable pageable = PageRequest.of(0, 10);
        Booking booking = BookingUtils.createExpectedBooking();
        Page<Booking> page = new PageImpl<>(Collections.singletonList(booking));
        BookingDto bookingDto = BookingUtils.createExpectedBookingDto();
        Page<BookingDto> expected = new PageImpl<>(Collections.singletonList(bookingDto));
        when(bookingRepository.findAll(pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(BookingUtils.createExpectedBookingDto());
        Page<BookingDto> actual = bookingService.getAllUserBookings(user, pageable);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Should get filtered bookings for admin and convert to dto")
    void getFilteredBookingsForAdminSuccess() {
        Long userId = 100L;
        BookingStatus status = BookingStatus.CONFIRMED;
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setStatus(status);
        booking.getUser().setId(userId);
        BookingDto bookingDto = BookingUtils.createExpectedBookingDto();
        bookingDto.setStatus(status);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Booking> pageOfEntities = new PageImpl<>(List.of(booking), pageable, 1);

        Page<BookingDto> expectedPageOfDtos = new PageImpl<>(List.of(bookingDto), pageable, 1);

        when(bookingRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pageOfEntities);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);
        Page<BookingDto> actualPageOfDtos = bookingService.getFilteredBookingsForAdmin(
                userId, status, pageable);
        assertThat(actualPageOfDtos)
                .usingRecursiveComparison()
                .isEqualTo(expectedPageOfDtos);
        verify(bookingRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    @DisplayName("Should get filtered bookings for owner and convert to dto")
    void getFilteredBookingsForOwnerSuccess() {
        Long userId = 100L;
        BookingStatus status = BookingStatus.CONFIRMED;
        User owner = UserUtils.createExpectedTestUserOwner();
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setStatus(status);
        booking.getUser().setId(userId);
        booking.getAccommodation().getOwner().setId(owner.getId());
        BookingDto bookingDto = BookingUtils.createExpectedBookingDto();
        bookingDto.setStatus(status);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> pageOfEntities = new PageImpl<>(List.of(booking), pageable, 1);
        Page<BookingDto> expectedPageOfDtos = new PageImpl<>(List.of(bookingDto), pageable, 1);
        when(bookingRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pageOfEntities);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);
        Page<BookingDto> actualPageOfDtos = bookingService.getFilteredBookingsForOwner(
                userId, status, pageable, owner);
        assertThat(actualPageOfDtos)
                .usingRecursiveComparison()
                .isEqualTo(expectedPageOfDtos);
        verify(bookingRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    @DisplayName("Should successfully delete booking by ID and send notifications")
    void deleteBookingByIdSuccess() {
        Long bookingId = 100L;
        Booking booking = BookingUtils.createExpectedBooking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingService.deleteBookingById(bookingId);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(telegramNotificationService, times(2))
                .sendNotification(anyString(), anyString());
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent booking")
    void deleteBookingByIdNotFound() {
        Long nonExistentBookingId = 999L;
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.deleteBookingById(nonExistentBookingId),
                "Expected deleteBookingById to throw EntityNotFoundException, but it didn't"
        );
        assertThat(thrown.getMessage()).isEqualTo("Booking with id: "
                + nonExistentBookingId + " not found.");
        verify(bookingRepository, times(1)).findById(nonExistentBookingId);
        verify(bookingRepository, never()).deleteById(anyLong());
        verify(telegramNotificationService, never()).sendNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should successfully update booking status and send notifications")
    void updateBookingStatusSuccess() {
        BookingStatus newStatus = BookingStatus.CONFIRMED;
        Booking booking = BookingUtils.createExpectedBooking();
        booking.setStatus(BookingStatus.PENDING);
        Booking updatedBooking = BookingUtils.createExpectedBooking();
        updatedBooking.setStatus(newStatus);
        BookingDto expectedDto = BookingUtils.createExpectedBookingDto();
        expectedDto.setStatus(newStatus);
        Long bookingId = 100L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(expectedDto);
        BookingDto actualDto = bookingService.updateBookingStatus(bookingId, newStatus);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(telegramNotificationService, times(2))
                .sendNotification(anyString(), anyString());
        verify(bookingMapper, times(1)).toDto(updatedBooking);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when "
            + "updating status of non-existent booking")
    void updateBookingStatusNotFound() {
        Long nonExistentBookingId = 999L;
        BookingStatus status = BookingStatus.CANCELED;
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.updateBookingStatus(nonExistentBookingId, status),
                "Expected updateBookingStatus to throw EntityNotFoundException, but it didn't"
        );
        assertThat(thrown.getMessage()).isEqualTo("Booking not found");

        verify(bookingRepository, times(1)).findById(nonExistentBookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toDto(any(Booking.class));
        verify(telegramNotificationService, never()).sendNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should successfully create a new booking")
    void createBookingSuccess() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(100L);
        createBookingDto.setUnitId(100L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 1));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));

        User user = UserUtils.createExpectedTestUserOwner();

        Accommodation accommodation = BookingUtils.createExpectedBooking().getAccommodation();
        AccommodationUnit unit = BookingUtils.createExpectedBooking().getUnit();

        Booking bookingEntityFromMapper = new Booking();
        bookingEntityFromMapper.setCheckInDate(createBookingDto.getCheckInDate());
        bookingEntityFromMapper.setCheckOutDate(createBookingDto.getCheckOutDate());
        bookingEntityFromMapper.setAccommodation(accommodation);
        bookingEntityFromMapper.setUnit(unit);
        bookingEntityFromMapper.setUser(user);
        bookingEntityFromMapper.setStatus(BookingStatus.PENDING);
        BigDecimal totalPrice = accommodation.getDailyPrice().multiply(
                new BigDecimal(ChronoUnit.DAYS.between(createBookingDto.getCheckInDate(),
                        createBookingDto.getCheckOutDate())));
        bookingEntityFromMapper.setTotalPrice(totalPrice);

        Booking savedBooking = BookingUtils.createExpectedBooking();
        savedBooking.setStatus(BookingStatus.PENDING);

        BookingDto expectedDto = BookingUtils.createExpectedBookingDto();
        expectedDto.setStatus(BookingStatus.PENDING);

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationUnitRepository.findById(createBookingDto.getUnitId()))
                .thenReturn(Optional.of(unit));

        when(bookingRepository.existsOverlappingActiveBooking(
                eq(unit.getId()), eq(createBookingDto.getCheckInDate()),
                eq(createBookingDto.getCheckOutDate()), anyList()))
                .thenReturn(false);

        when(bookingMapper.toBooking(any(CreateBookingDto.class)))
                .thenReturn(bookingEntityFromMapper);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto actualDto = bookingService.createBooking(createBookingDto, user);

        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, times(1))
                .findById(createBookingDto.getUnitId());
        verify(bookingRepository, times(1)).existsOverlappingActiveBooking(
                eq(unit.getId()), eq(createBookingDto.getCheckInDate()),
                eq(createBookingDto.getCheckOutDate()), anyList());
        verify(bookingMapper, times(1)).toBooking(createBookingDto);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verify(telegramNotificationService, times(2))
                .sendNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating booking "
            + "for non-existent accommodation")
    void createBookingAccommodationNotFound() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(999L);
        createBookingDto.setUnitId(100L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 1));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));
        User user = UserUtils.createExpectedTestUserOwner();

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(createBookingDto, user),
                "Expected EntityNotFoundException for non-existent accommodation"
        );
        assertThat(thrown.getMessage()).isEqualTo("Accommodation not found by id: "
                + createBookingDto.getAccommodationId());

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toBooking(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating booking for non-existent unit")
    void createBookingUnitNotFound() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(100L);
        createBookingDto.setUnitId(999L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 1));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));
        User user = UserUtils.createExpectedTestUserOwner();

        Accommodation accommodation = BookingUtils.createExpectedBooking().getAccommodation();

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationUnitRepository.findById(createBookingDto.getUnitId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(createBookingDto, user),
                "Expected EntityNotFoundException for non-existent unit"
        );
        assertThat(thrown.getMessage()).isEqualTo("Unit not found by id: "
                + createBookingDto.getUnitId());

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, times(1))
                .findById(createBookingDto.getUnitId());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toBooking(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unit does not belong to accommodation")
    void createBookingUnitDoesNotBelongToAccommodation() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(100L);
        createBookingDto.setUnitId(101L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 1));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));

        AccommodationUnit unit = new AccommodationUnit();
        unit.setId(101L);
        Accommodation anotherAccommodation = new Accommodation();
        anotherAccommodation.setId(999L);
        unit.setAccommodation(anotherAccommodation);
        Accommodation accommodation = BookingUtils.createExpectedBooking().getAccommodation();

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationUnitRepository.findById(createBookingDto.getUnitId()))
                .thenReturn(Optional.of(unit));
        User user = UserUtils.createExpectedTestUserOwner();

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(createBookingDto, user),
                "Expected IllegalArgumentException for unit not belonging to accommodation"
        );
        assertThat(thrown.getMessage()).isEqualTo("Unit with id: "
                + createBookingDto.getUnitId()
                + " is not a unit of accommodation with id: "
                + createBookingDto.getAccommodationId());

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, times(1))
                .findById(createBookingDto.getUnitId());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toBooking(any());
    }

    @Test
    @DisplayName("Should throw BookingUnavailableException when unit is not available")
    void createBookingUnitNotAvailable() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(100L);
        createBookingDto.setUnitId(100L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 1));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));
        User user = UserUtils.createExpectedTestUserOwner();

        Accommodation accommodation = BookingUtils.createExpectedBooking().getAccommodation();
        AccommodationUnit unit = BookingUtils.createExpectedBooking().getUnit();

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationUnitRepository.findById(createBookingDto.getUnitId()))
                .thenReturn(Optional.of(unit));

        when(bookingRepository.existsOverlappingActiveBooking(
                eq(unit.getId()), eq(createBookingDto.getCheckInDate()),
                eq(createBookingDto.getCheckOutDate()), anyList()))
                .thenReturn(true);

        BookingUnavailableException thrown = assertThrows(
                BookingUnavailableException.class,
                () -> bookingService.createBooking(createBookingDto, user),
                "Expected BookingUnavailableException when unit is not available"
        );
        assertThat(thrown.getMessage()).isEqualTo("Booking unavailable for unit "
                + "id: 100 in date range 2025-09-01 to 2025-09-07. "
                + "Please choose another date or accommodation.");

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, times(1))
                .findById(createBookingDto.getUnitId());
        verify(bookingRepository, times(1))
                .existsOverlappingActiveBooking(
                eq(unit.getId()), eq(createBookingDto.getCheckInDate()),
                        eq(createBookingDto.getCheckOutDate()), anyList());
        verify(bookingMapper, never()).toBooking(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when check-in date "
            + "is after or equal to check-out date")
    void createBookingInvalidDates() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setAccommodationId(100L);
        createBookingDto.setUnitId(100L);
        createBookingDto.setCheckInDate(LocalDate.of(2025, 9, 7));
        createBookingDto.setCheckOutDate(LocalDate.of(2025, 9, 7));

        Accommodation accommodation = BookingUtils.createExpectedBooking().getAccommodation();
        AccommodationUnit unit = BookingUtils.createExpectedBooking().getUnit();

        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationUnitRepository.findById(createBookingDto.getUnitId()))
                .thenReturn(Optional.of(unit));

        when(bookingRepository.existsOverlappingActiveBooking(
                eq(unit.getId()), eq(createBookingDto.getCheckInDate()),
                eq(createBookingDto.getCheckOutDate()), anyList()))
                .thenReturn(false);

        Booking bookingFromMapper = BookingUtils.createExpectedBooking();
        bookingFromMapper.setId(null);
        if (bookingFromMapper.getAccommodation() != null) {
            bookingFromMapper.getAccommodation().setId(null);
            if (bookingFromMapper.getAccommodation().getLocation() != null) {
                bookingFromMapper.getAccommodation().getLocation().setId(100L);
            }
            if (bookingFromMapper.getAccommodation().getPhotos() != null) {
                bookingFromMapper.getAccommodation().getPhotos().forEach(
                        p -> p.setId(100L));
            }
            if (bookingFromMapper.getAccommodation().getUnits() != null) {
                bookingFromMapper.getAccommodation().getUnits().forEach(
                        u -> u.setId(100L));
            }
        }
        User user = UserUtils.createExpectedTestUserOwner();
        if (bookingFromMapper.getUnit() != null) {
            bookingFromMapper.getUnit().setId(null);
        }
        bookingFromMapper.setUser(user);
        bookingFromMapper.setStatus(BookingStatus.PENDING);

        when(bookingMapper.toBooking(any(CreateBookingDto.class)))
                .thenReturn(bookingFromMapper);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(createBookingDto, user),
                "Expected IllegalArgumentException for invalid dates"
        );
        assertThat(thrown.getMessage()).isEqualTo("Invalid booking date range: "
                + "check-in 2025-09-07, check-out 2025-09-07");

        verify(accommodationRepository, times(1))
                .findById(createBookingDto.getAccommodationId());
        verify(accommodationUnitRepository, times(1))
                .findById(createBookingDto.getUnitId());
        verify(bookingRepository, times(1))
                .existsOverlappingActiveBooking(any(), any(), any(), anyList());
        verify(bookingMapper, times(1))
                .toBooking(any(CreateBookingDto.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
