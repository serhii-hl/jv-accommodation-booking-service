package app.repository;

import app.model.Accommodation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    @Query("SELECT a.owner.id FROM Accommodation a WHERE a.id = :accommodationId")
    Long getOwnerIdByAccommodationId(@Param("accommodationId") Long accommodationId);

    @Query("SELECT a FROM Accommodation a WHERE a.id = :id AND a.isDeleted = false")
    Optional<Accommodation> findById(@Param("id") Long id);

    @Query("SELECT a FROM Accommodation a WHERE a.isDeleted = false")
    Page<Accommodation> findAll(Pageable pageable);
}
