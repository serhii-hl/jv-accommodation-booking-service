package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.CreateAccommodationUnitDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.dto.location.LocationDto;
import app.mapper.AccommodationMapper;
import app.model.Accommodation;
import app.model.User;
import app.repository.AccommodationRepository;
import app.service.impl.AccommodationPhotoServiceImpl;
import app.service.impl.AccommodationServiceImpl;
import app.service.impl.AccommodationUnitServiceImpl;
import app.service.impl.LocationServiceImpl;
import app.util.AccommodationUtils;
import app.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTests {
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private LocationServiceImpl locationService;

    @Mock
    private AccommodationPhotoServiceImpl accommodationPhotoService;

    @Mock
    private AccommodationUnitServiceImpl unitService;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Test
    @DisplayName("Should get all accommodations and convert to dto")
    void getAllAccommodationSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Accommodation accommodation = AccommodationUtils.createExpectedAccommodation();
        AccommodationDto accommodationDto = AccommodationUtils.createExpectedAccommodationDto();
        Page<Accommodation> pageOfEntities = new PageImpl<>(List.of(accommodation), pageable, 1);
        Page<AccommodationDto> expectedPageOfDtos = new PageImpl<>(List.of(accommodationDto),
                pageable, 1);
        when(accommodationRepository.findAll(pageable)).thenReturn(pageOfEntities);
        when(accommodationMapper.toDto(accommodation)).thenReturn(accommodationDto);
        Page<AccommodationDto> actualPageOfDtos = accommodationService
                .getAllAccommodations(pageable);
        assertPageContentEquals(actualPageOfDtos, expectedPageOfDtos);
        verify(accommodationRepository).findAll(pageable);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("Should get accommodation by id and convert to dto")
    void getAccommodationByIdSuccess() {
        Accommodation accommodation = AccommodationUtils.createExpectedAccommodation();
        AccommodationDto accommodationDto = AccommodationUtils.createExpectedAccommodationDto();
        when(accommodationRepository.findById(100L)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(accommodationDto);
        AccommodationDto actualDto = accommodationService.getAccommodationById(100L);
        assertEquals(accommodationDto, actualDto);
        verify(accommodationRepository).findById(100L);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    @DisplayName("Should successfully delete accommodation by ID if it exists")
    void deleteAccommodationByIdSuccess() {
        Long accommodationId = 100L;
        when(accommodationRepository.existsById(accommodationId)).thenReturn(true);
        accommodationService.deleteAccommodationById(accommodationId);
        verify(accommodationRepository, times(1)).existsById(accommodationId);
        verify(accommodationRepository, times(1)).deleteById(accommodationId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent accommodation")
    void deleteAccommodationByIdNotFound() {
        Long nonExistentAccommodationId = 999L;
        when(accommodationRepository.existsById(nonExistentAccommodationId)).thenReturn(false);
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.deleteAccommodationById(nonExistentAccommodationId),
                "Expected deleteAccommodationById to throw EntityNotFoundException, but it didn't"
        );

        assertThat(thrown.getMessage()).isEqualTo("Accommodation with id: "
                + nonExistentAccommodationId + " not found.");
        verify(accommodationRepository, times(1))
                .existsById(nonExistentAccommodationId);
        verify(accommodationRepository, never()).deleteById(nonExistentAccommodationId);
    }

    @Test
    @DisplayName("Should successfully update accommodation with new daily price")
    void updateAccommodationSuccess() {
        Long accommodationId = 100L;
        BigDecimal newDailyPrice = new BigDecimal("80.50");
        Accommodation initialAccommodation = AccommodationUtils.createExpectedAccommodation();
        UpdateAccommodationDto updateAccommodationDto = new UpdateAccommodationDto();
        updateAccommodationDto.setDailyPrice(newDailyPrice);
        AccommodationDto expectedDto = AccommodationUtils.createExpectedAccommodationDto();
        expectedDto.setDailyPrice(newDailyPrice);
        when(accommodationRepository.findById(accommodationId))
                .thenReturn(Optional.of(initialAccommodation));
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(expectedDto);
        AccommodationDto actualDto = accommodationService.updateAccommodationById(
                accommodationId, updateAccommodationDto);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);

        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
        verify(accommodationMapper, times(1)).toDto(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when accommodation to update is not found")
    void updateAccommodationNotFound() {
        Long nonExistentId = 999L;
        UpdateAccommodationDto updateAccommodationDto = new UpdateAccommodationDto();

        when(accommodationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.updateAccommodationById(
                        nonExistentId, updateAccommodationDto),
                "Expected EntityNotFoundException, but it was not thrown"
        );

        assertThat(thrown.getMessage()).isEqualTo("Can`t find accommodation by id "
                + nonExistentId);

        verify(accommodationRepository, times(1)).findById(nonExistentId);
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(accommodationMapper, never()).toDto(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should successfully create accommodation")
    void createAccommodationSuccess() {
        CreateAccommodationDto createAccommodationDto = new CreateAccommodationDto();
        Accommodation initialAccommodation = AccommodationUtils.createExpectedAccommodation();
        User user = UserUtils.createExpectedTestUserOwner();
        initialAccommodation.setOwner(user);
        createAccommodationDto.setLocation(AccommodationUtils.createExpectedLocationDto());
        createAccommodationDto.setInitialPhotoUrls(Set.of("http://example.com/photo1.jpg", "http://example.com/photo2.jpg"));
        createAccommodationDto.setUnits(Set.of(new CreateAccommodationUnitDto()));
        when(accommodationMapper.toEntity(any(CreateAccommodationDto.class)))
                .thenReturn(initialAccommodation);
        when(locationService.createLocation(any(LocationDto.class)))
                .thenReturn(initialAccommodation.getLocation());
        when(accommodationPhotoService
                .createPhotosForAccommodation(any(Set.class), any(Accommodation.class)))
                .thenReturn(initialAccommodation.getPhotos());
        when(unitService.createUnitsForAccommodation(any(Set.class), any(Accommodation.class)))
                .thenReturn(initialAccommodation.getUnits());
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AccommodationDto expectedDto = AccommodationUtils.createExpectedAccommodationDto();
        when(accommodationMapper.toDto(any(Accommodation.class))).thenReturn(expectedDto);
        AccommodationDto actualDto = accommodationService
                .createAccommodation(createAccommodationDto, user);
        assertThat(actualDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
        verify(accommodationMapper, times(1))
                .toEntity(createAccommodationDto);
        verify(locationService, times(1))
                .createLocation(createAccommodationDto.getLocation());
        verify(accommodationPhotoService, times(1))
                .createPhotosForAccommodation(
                        any(Set.class),
                        any(Accommodation.class)
                );
        verify(unitService, times(1))
                .createUnitsForAccommodation(
                        any(Set.class),
                        any(Accommodation.class)
                );
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
        verify(accommodationMapper, times(1)).toDto(any(Accommodation.class));
    }

    @Test
    @DisplayName("Should return owner`s id")
    void getOwnerIdForAccommodationSuccess() {
        User expectedUser = UserUtils.createExpectedTestUserOwner();
        when(accommodationRepository.getOwnerIdByAccommodationId(100L))
                .thenReturn(expectedUser.getId());

        assertEquals(expectedUser.getId(), accommodationRepository
                .getOwnerIdByAccommodationId(100L));

        verify(accommodationRepository, times(1))
                .getOwnerIdByAccommodationId(100L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when owner is not found")
    void getOwnerIdForAccommodationNotFound() {
        Long nonExistentId = 999L;
        when(accommodationRepository.getOwnerIdByAccommodationId(999L)).thenReturn(null);

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.getOwnerIdForAccommodation(nonExistentId),
                "Expected EntityNotFoundException, but it was not thrown"
        );

        assertThat(thrown.getMessage()).isEqualTo("Accommodation with id: "
                + nonExistentId + " not found or has no owner");

        verify(accommodationRepository, times(1)).getOwnerIdByAccommodationId(nonExistentId);
    }

    private void assertPageContentEquals(Page<AccommodationDto> actualPageOfDtos,
                                         Page<AccommodationDto> expectedPageOfDtos) {
        assertThat(actualPageOfDtos).isNotNull();
        assertThat(actualPageOfDtos.getTotalElements())
                .isEqualTo(expectedPageOfDtos.getTotalElements());
        assertThat(actualPageOfDtos.getTotalPages()).isEqualTo(expectedPageOfDtos.getTotalPages());
        assertThat(actualPageOfDtos.getNumber()).isEqualTo(expectedPageOfDtos.getNumber());
        assertThat(actualPageOfDtos.getSize()).isEqualTo(expectedPageOfDtos.getSize());
        assertThat(actualPageOfDtos.getContent()).hasSize(expectedPageOfDtos.getContent().size());
        assertThat(actualPageOfDtos.getContent())
                .usingRecursiveComparison()
                .isEqualTo(expectedPageOfDtos.getContent());
    }
}
