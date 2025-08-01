package app.repository;

import app.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    @Query("SELECT p FROM Payment p "
            + "WHERE p.booking.user.id = :userId AND p.isDeleted = false")
    Page<Payment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);
}
