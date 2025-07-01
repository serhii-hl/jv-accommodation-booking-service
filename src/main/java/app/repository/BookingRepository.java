package app.repository;

import app.model.Booking;
import app.model.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.accommodation "
            + "LEFT JOIN FETCH b.unit WHERE b.id = :id AND b.isDeleted = false")
    Optional<Booking> findById(@Param("id") Long id);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.accommodation "
            + "LEFT JOIN FETCH b.unit WHERE b.isDeleted = false")
    Page<Booking> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"accommodation", "unit"})
    Page<Booking> findAll(Specification<Booking> spec, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.unit.id = :unitId "
            + "AND b.status IN :blockingStatuses "
            + "AND (b.checkInDate < :requestedCheckOut AND b.checkOutDate > :requestedCheckIn)")
    boolean existsOverlappingActiveBooking(
            @Param("unitId") Long unitId,
            @Param("requestedCheckIn") LocalDate requestedCheckIn,
            @Param("requestedCheckOut") LocalDate requestedCheckOut,
            @Param("blockingStatuses") List<BookingStatus> blockingStatuses
    );
}
