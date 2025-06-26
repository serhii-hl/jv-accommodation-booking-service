package app.repository;

import app.model.Booking;
import app.model.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {
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
