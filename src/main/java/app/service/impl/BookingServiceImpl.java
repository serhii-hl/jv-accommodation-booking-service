package app.service.impl;

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
import app.service.AccommodationService;
import app.service.BookingService;
import app.specification.BookingSpecification;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationService accommodationService;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationUnitRepository accommodationUnitRepository;

    @Override
    public BookingDto getBookingById(Long id) {
        return bookingMapper.toDto(bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found")));
    }

    @Override
    public Page<BookingDto> getAllUserBookings(User user, Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> getFilteredBookingsForAdmin(
            Long userId, BookingStatus status, Pageable pageable) {
        Specification<Booking> spec = Specification
                .where(BookingSpecification.isNotDeleted())
                .and(BookingSpecification.hasUserId(userId))
                .and(BookingSpecification.hasStatus(status));

        return bookingRepository.findAll(spec, pageable).map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> getFilteredBookingsForOwner(
            Long userId, BookingStatus status, Pageable pageable, User owner) {
        Specification<Booking> spec = Specification
                .where(BookingSpecification.isNotDeleted())
                .and(BookingSpecification.hasUserId(userId))
                .and(BookingSpecification.hasStatus(status))
                .and(BookingSpecification.hasAccommodationOwnerId(owner.getId()));

        return bookingRepository.findAll(spec, pageable).map(bookingMapper::toDto);
    }

    @Override
    public void deleteBookingById(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("Booking with id: " + id + " not found.");
        }
        bookingRepository.deleteById(id);
    }

    @Override
    public BookingDto updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        booking.setStatus(status);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto, User user) {
        Accommodation accommodation = accommodationRepository
                .findById(createBookingDto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation not found by id: " + createBookingDto.getAccommodationId()));
        AccommodationUnit unit = accommodationUnitRepository.findById(createBookingDto.getUnitId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unit not found by id: " + createBookingDto.getUnitId()));
        if (!unit.getAccommodation().getId().equals(accommodation.getId())) {
            throw new IllegalArgumentException("Unit with id: " + createBookingDto.getUnitId()
                    + " is not a unit of accommodation with id: "
                    + createBookingDto.getAccommodationId());
        }
        checkUnitAvailability(unit,
                createBookingDto.getCheckInDate(),
                createBookingDto.getCheckOutDate());

        Booking booking = bookingMapper.toBooking(createBookingDto);
        booking.setAccommodation(accommodation);
        booking.setUnit(unit);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);
        BigDecimal totalPrice = priceCalculator(createBookingDto.getCheckInDate(),
                createBookingDto.getCheckOutDate(), accommodation.getDailyPrice());
        booking.setTotalPrice(totalPrice);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    private void checkUnitAvailability(AccommodationUnit unit,
                                       LocalDate checkIn, LocalDate checkOut) {
        List<BookingStatus> blockingStatuses = List.of(
                BookingStatus.PENDING, BookingStatus.CONFIRMED);
        boolean isOverlapping = bookingRepository.existsOverlappingActiveBooking(
                unit.getId(), checkIn, checkOut, blockingStatuses
        );
        if (isOverlapping) {
            throw new BookingUnavailableException(
                    "Booking is not available in these days, "
                            + "please choose another date or accommodation");
        }
    }

    private BigDecimal priceCalculator(LocalDate checkIn, LocalDate checkOut,
                                       BigDecimal dayPrice) {
        long numberOfDays = ChronoUnit.DAYS
                .between(checkIn, checkOut);
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Booking has no days");
        }
        return dayPrice.multiply(new BigDecimal(numberOfDays));
    }
}
