package app.repository;

import app.model.AccommodationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccommodationPhotoRepository extends JpaRepository<AccommodationPhoto, Long>,
        JpaSpecificationExecutor<AccommodationPhoto> {

}
