package app.service.impl;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.mapper.AccommodationMapper;
import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.model.AccommodationUnit;
import app.model.Location;
import app.model.User;
import app.repository.AccommodationRepository;
import app.service.AccommodationPhotoService;
import app.service.AccommodationService;
import app.service.AccommodationUnitService;
import app.service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final LocationService locationService;
    private final AccommodationUnitService unitService;
    private final AccommodationPhotoService accommodationPhotoService;

    @Override
    public Page<AccommodationDto> getAllAccommodations(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find accommodation by id " + id));
    }

    @Override
    public void deleteAccommodationById(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new EntityNotFoundException("Accommodation with id: " + id + " not found.");
        }
        accommodationRepository.deleteById(id);
    }

    @Override
    public AccommodationDto updateAccommodationById(
            Long id, UpdateAccommodationDto updateAccommodationDto) {
        Accommodation accommodation = accommodationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find accommodation by id "
                        + id));
        if (updateAccommodationDto.getType() != null) {
            accommodation.setType(updateAccommodationDto.getType());
        }
        if (updateAccommodationDto.getLocation() != null) {
            accommodation.setLocation(locationService
                    .updateLocationById(updateAccommodationDto.getLocation(), id));
        }
        if (updateAccommodationDto.getAmenitySet() != null) {
            accommodation.setAmenitySet(updateAccommodationDto.getAmenitySet());
        }
        if (updateAccommodationDto.getSize() != null) {
            accommodation.setSize(updateAccommodationDto.getSize());
        }
        if (updateAccommodationDto.getDailyPrice() != null) {
            accommodation.setDailyPrice(updateAccommodationDto.getDailyPrice());
        }
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public AccommodationDto createAccommodation(
            CreateAccommodationDto createAccommodationDto, User user) {
        Accommodation accommodation = accommodationMapper.toEntity(createAccommodationDto);
        accommodation.setOwner(user);
        Location location = locationService.createLocation(createAccommodationDto.getLocation());
        accommodation.setLocation(location);
        location.setAccommodation(accommodation);
        List<AccommodationPhoto> photos = accommodationPhotoService.createPhotosForAccommodation(
                createAccommodationDto.getInitialPhotoUrls(),
                accommodation
        );
        accommodation.setPhotos(photos);
        List<AccommodationUnit> units = unitService.createUnitsForAccommodation(
                createAccommodationDto.getUnits(),
                accommodation
        );
        accommodation.setUnits(units);
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public Long getOwnerIdForAccommodation(Long accommodationId) {
        Long ownerId = accommodationRepository.getOwnerIdByAccommodationId(accommodationId);
        if (ownerId == null) {
            throw new EntityNotFoundException(
                    "Accommodation with id: " + accommodationId + " not found or has no owner");
        }
        return ownerId;
    }

}
