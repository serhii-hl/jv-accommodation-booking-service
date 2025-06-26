package app.repository;

import app.model.AccommodationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccommodationUnitRepository extends JpaRepository<AccommodationUnit, Long>,
        JpaSpecificationExecutor<AccommodationUnit> {
}
