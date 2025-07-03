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
import app.service.BookingService;
import app.service.TelegramNotificationService;
import app.specification.BookingSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final TelegramNotificationService telegramNotificationService;
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
    @Transactional
    public void deleteBookingById(Long id) {
        Booking booking = bookingRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Booking with id: " + id + " not found."));
        sendTgMessageToUser(booking, generateTgMessageToAllAfterDelete(booking));
        sendTgMessageToOwner(booking, generateTgMessageToAllAfterDelete(booking));
        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        booking.setStatus(status);
        Booking savedBooking = bookingRepository.save(booking);
        sendTgMessageToUser(savedBooking, generateTgMessageToAllAfterUpdate(savedBooking));
        sendTgMessageToOwner(savedBooking, generateTgMessageToAllAfterUpdate(savedBooking));
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
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
        Booking savedBooking = bookingRepository.save(booking);
        sendTgMessageToUser(savedBooking, generateTgMessageToUserAfterBooking(savedBooking));
        sendTgMessageToOwner(savedBooking, generateTgMessageToOwnerAfterBooking(savedBooking));
        return bookingMapper.toDto(savedBooking);
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

    private void sendTgMessageToUser(Booking booking, String message) {
        String telegramChatId = booking.getUser().getTelegramChatId();
        if (telegramChatId != null) {
            telegramNotificationService.sendNotification(telegramChatId,
                    message);
        }
    }

    private void sendTgMessageToOwner(Booking booking, String message) {
        String telegramChatId = booking.getAccommodation().getOwner().getTelegramChatId();
        if (telegramChatId != null) {
            telegramNotificationService.sendNotification(telegramChatId,
                    message);
        }
    }

    private String generateTgMessageToUserAfterBooking(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address of your accommodation is: ")
                .append(booking.getAccommodation().getLocation().getCity())
                .append(", ").append(booking.getAccommodation().getLocation().getStreet())
                .append(" ").append(booking.getAccommodation().getLocation().getNumber())
                .append(" and your accommodation booked in ")
                .append(booking.getCheckInDate())
                .append(" - ")
                .append(booking.getCheckOutDate())
                .append(". Phone number of owner is: ")
                .append(booking.getAccommodation().getOwner().getPhoneNumber())
                .append(". Payment status: ")
                .append(booking.getStatus())
                .append(". Booking id: ")
                .append(booking.getId());
        return sb.toString();
    }

    private String generateTgMessageToOwnerAfterBooking(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("User with email ")
                .append(booking.getUser().getEmail())
                .append(" booked your accommodation in dates ")
                .append(booking.getCheckInDate())
                .append(" - ")
                .append(booking.getCheckOutDate())
                .append("payment status: ")
                .append(booking.getStatus())
                .append(". Booking id: ")
                .append(booking.getId());
        return sb.toString();
    }

    private String generateTgMessageToAllAfterUpdate(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking with id ")
                .append(booking.getId())
                .append(" updated payment status to ")
                .append(booking.getStatus());
        return sb.toString();
    }

    private String generateTgMessageToAllAfterDelete(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking with id ")
                .append(booking.getId())
                .append(" was deleted... ");
        return sb.toString();
    }

}
