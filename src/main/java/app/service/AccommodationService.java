package app.service;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    Page<AccommodationDto> getAllAccommodations(Pageable pageable);

    AccommodationDto getAccommodationById(Long id);

    void deleteAccommodationById(Long id);

    AccommodationDto updateAccommodationById(Long id,
                                             UpdateAccommodationDto updateAccommodationDto);

    AccommodationDto createAccommodation(CreateAccommodationDto createAccommodationDto, User user);

    Long getOwnerIdForAccommodation(Long accommodationId);
}
