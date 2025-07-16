package app.service;

import static org.assertj.core.api.Assertions.assertThat;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.service.impl.AccommodationUnitServiceImpl;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccommodationUnitServiceTests {
    @InjectMocks
    private AccommodationUnitServiceImpl accommodationUnitService;

    @Test
    @DisplayName("Should correctly create set of AccommodationUnit objects")
    void createUnitsForAccommodationSuccess() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(100L);
        CreateAccommodationUnitDto createAccommodationUnitDto = new CreateAccommodationUnitDto();
        createAccommodationUnitDto.setUnitNumber("1");
        createAccommodationUnitDto.setActive(true);
        AccommodationUnit expectedUnit = new AccommodationUnit();
        expectedUnit.setUnitNumber("1");
        expectedUnit.setActive(true);
        expectedUnit.setAccommodation(accommodation);
        Set<AccommodationUnit> expectedUnits = new HashSet<>();
        expectedUnits.add(expectedUnit);
        Set<CreateAccommodationUnitDto> unitDtos = Set.of(createAccommodationUnitDto);
        Set<AccommodationUnit> actualUnits = accommodationUnitService
                .createUnitsForAccommodation(unitDtos, accommodation);
        assertThat(actualUnits).hasSize(1);
        assertThat(actualUnits)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(expectedUnits);
        assertThat(actualUnits.iterator().next().getAccommodation()).isEqualTo(accommodation);
    }

}
