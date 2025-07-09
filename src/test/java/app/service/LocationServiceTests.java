package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import app.dto.location.LocationDto;
import app.model.Location;
import app.repository.LocationRepository;
import app.service.impl.LocationServiceImpl;
import app.util.AccommodationUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {
    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @Test
    @DisplayName("update location by id success")
    void updateLocationByIdSuccess() {
        LocationDto locationDto = AccommodationUtils.createExpectedLocationDto();
        Location expected = AccommodationUtils.createExpectedLocation();
        when(locationRepository.findById(100L)).thenReturn(Optional.of(expected));
        Location actual = locationService.updateLocationById(locationDto, 100L);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
