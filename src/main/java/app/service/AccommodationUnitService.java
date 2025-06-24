package app.service;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import java.util.List;

public interface AccommodationUnitService {
    List<AccommodationUnit> createUnitsForAccommodation(
            List<CreateAccommodationUnitDto> unitDtos, Accommodation accommodation);
}
