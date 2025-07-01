package app.service;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import java.util.Set;

public interface AccommodationUnitService {
    Set<AccommodationUnit> createUnitsForAccommodation(
            Set<CreateAccommodationUnitDto> unitDtos, Accommodation accommodation);
}
