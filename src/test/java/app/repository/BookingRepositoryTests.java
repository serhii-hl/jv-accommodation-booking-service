package app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.model.Booking;
import app.model.BookingStatus;
import app.specification.BookingSpecification;
import app.util.BookingUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/booking/add-booking-to-test-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/booking/remove-booking-from-test-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingRepositoryTests {
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private static final Long TEST_UNIT_ID = 100L;
    private static final List<BookingStatus> BLOCKING_STATUSES = Arrays.asList(
            BookingStatus.CONFIRMED, BookingStatus.PENDING);

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Find booking by id ")
    void findBookingByIdTest() {
        Optional<Booking> result = bookingRepository.findById(100L);
        Optional<Booking> expected = Optional.of(BookingUtils.createExpectedBooking());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Find all bookings ")
    void findAllBookingTest() {
        Page<Booking> result = bookingRepository.findAll(PAGEABLE);
        Booking expectedBooking = BookingUtils.createExpectedBooking();
        Page<Booking> expected = new PageImpl<>(
                Collections.singletonList(expectedBooking),
                PAGEABLE, 1
        );
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("should find the booking by its user ID")
    void hasUserIdSpecificationTest() {
        Long userId = 100L;
        Specification<Booking> spec = BookingSpecification.hasUserId(userId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should not find booking if user ID does not match")
    void hasUserIdSpecificationNoMatchTest() {
        Long nonMatchingUserId = 999L;
        Specification<Booking> spec = BookingSpecification.hasUserId(nonMatchingUserId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should find the booking by its status")
    void hasStatusSpecificationTest() {
        BookingStatus status = BookingStatus.CONFIRMED;
        Specification<Booking> spec = BookingSpecification.hasStatus(status);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("should not find booking if status does not match")
    void hasStatusSpecificationNoMatchTest() {
        BookingStatus nonMatchingStatus = BookingStatus.PENDING;
        Specification<Booking> spec = BookingSpecification.hasStatus(nonMatchingStatus);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should find the booking by its accommodation ID")
    void hasAccommodationIdSpecificationTest() {
        Long accommodationId = 100L;
        Specification<Booking> spec = BookingSpecification
                .hasAccommodationId(accommodationId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        assertThat(result.getContent().get(0).getAccommodation().getId())
                .isEqualTo(accommodationId);
    }

    @Test
    @DisplayName("should not find booking if accommodation ID does not match")
    void hasAccommodationIdSpecificationNoMatchTest() {
        Long nonMatchingAccommodationId = 999L;
        Specification<Booking> spec = BookingSpecification
                .hasAccommodationId(nonMatchingAccommodationId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should find the booking by its accommodation owner ID")
    void hasAccommodationOwnerIdSpecificationTest() {
        Long ownerId = 100L;
        Specification<Booking> spec = BookingSpecification.hasAccommodationOwnerId(ownerId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        assertThat(result.getContent().get(0).getAccommodation().getOwner()
                .getId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("should not find booking if accommodation owner ID does not match")
    void hasAccommodationOwnerIdSpecificationNoMatchTest() {
        Long nonMatchingOwnerId = 999L;
        Specification<Booking> spec = BookingSpecification
                .hasAccommodationOwnerId(nonMatchingOwnerId);

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should find non-deleted bookings (explicitly using isNotDeleted)")
    void isNotDeletedSpecificationTest() {
        Specification<Booking> spec = BookingSpecification.isNotDeleted();

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        assertThat(result.getContent().get(0).isDeleted()).isFalse();
    }

    @Test
    @DisplayName("should find booking when combining multiple matching specifications")
    void combinedMatchingSpecificationsTest() {
        Long userId = 100L;
        BookingStatus status = BookingStatus.CONFIRMED;
        Long accommodationId = 100L;
        Long ownerId = 100L;

        Specification<Booking> spec = Specification.where(BookingSpecification
                        .hasUserId(userId))
                .and(BookingSpecification.hasStatus(status))
                .and(BookingSpecification.hasAccommodationId(accommodationId))
                .and(BookingSpecification.hasAccommodationOwnerId(ownerId));
        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should return empty page when combining specifications "
            + "with one non-matching criteria")
    void combinedNonMatchingSpecificationsTest() {
        Long userId = 100L;
        BookingStatus status = BookingStatus.CONFIRMED;
        Long nonMatchingAccommodationId = 999L;

        Specification<Booking> spec = Specification.where(BookingSpecification.hasUserId(userId))
                .and(BookingSpecification.hasStatus(status))
                .and(BookingSpecification.hasAccommodationId(nonMatchingAccommodationId));

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should return all non-deleted bookings if specification parameters are null")
    void nullSpecificationParametersTest() {
        Specification<Booking> spec1 = BookingSpecification.hasUserId(null);
        Specification<Booking> spec2 = BookingSpecification.hasStatus(null);
        Specification<Booking> combinedSpec = Specification.where(spec1).and(spec2);

        Page<Booking> result = bookingRepository.findAll(combinedSpec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should return all non-deleted bookings if specification itself is null")
    void directNullSpecificationTest() {
        Specification<Booking> spec = null;

        Page<Booking> result = bookingRepository.findAll(spec, PAGEABLE);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should return true when requested dates overlap with an existing "
            + "confirmed booking")
    void existsOverlappingActiveBooking_simpleOverlap_true() {
        LocalDate requestedCheckIn = LocalDate.of(2025, 8, 3);
        LocalDate requestedCheckOut = LocalDate.of(2025, 8, 5);

        boolean result = bookingRepository.existsOverlappingActiveBooking(
                TEST_UNIT_ID, requestedCheckIn, requestedCheckOut, BLOCKING_STATUSES);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when requested dates do not overlap with existing booking")
    void existsOverlappingActiveBooking_noOverlap_false() {
        LocalDate requestedCheckIn = LocalDate.of(2025, 8, 7);
        LocalDate requestedCheckOut = LocalDate.of(2025, 8, 10);

        boolean result = bookingRepository.existsOverlappingActiveBooking(
                TEST_UNIT_ID, requestedCheckIn, requestedCheckOut, BLOCKING_STATUSES);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false if unit ID does not match")
    void existsOverlappingActiveBooking_wrongUnitId_false() {

        LocalDate requestedCheckIn = LocalDate.of(2025, 8, 3);
        LocalDate requestedCheckOut = LocalDate.of(2025, 8, 5);

        boolean result = bookingRepository.existsOverlappingActiveBooking(
                999L, requestedCheckIn, requestedCheckOut, BLOCKING_STATUSES);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false if booking status is not in blocking statuses "
            + "(e.g., if existing booking were CANCELLED)")
    void existsOverlappingActiveBooking_nonBlockingStatus_false() {

        List<BookingStatus> nonBlockingList = Arrays.asList(BookingStatus.EXPIRED,
                BookingStatus.CANCELED);

        LocalDate requestedCheckIn = LocalDate.of(2025, 8, 3);
        LocalDate requestedCheckOut = LocalDate.of(2025, 8, 5);

        boolean result = bookingRepository.existsOverlappingActiveBooking(
                TEST_UNIT_ID, requestedCheckIn, requestedCheckOut, nonBlockingList);

        assertThat(result).isFalse();
    }
}
