package app.repository;

import app.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {
    @Query("SELECT a.owner.id FROM Accommodation a WHERE a.id = :accommodationId")
    Long getOwnerIdByAccommodationId(@Param("accommodationId") Long accommodationId);
}
