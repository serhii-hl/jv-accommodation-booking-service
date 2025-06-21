package app.repository;

import app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

}
