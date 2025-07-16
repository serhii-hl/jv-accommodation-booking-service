package app.service.impl;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.service.AccommodationUnitService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationUnitServiceImpl implements AccommodationUnitService {

    @Override
    public Set<AccommodationUnit> createUnitsForAccommodation(
            Set<CreateAccommodationUnitDto> unitDtos, Accommodation accommodation) {
        Set<AccommodationUnit> units = new HashSet<>();
        if (unitDtos != null && !unitDtos.isEmpty()) {
            for (CreateAccommodationUnitDto unitDto : unitDtos) {
                AccommodationUnit unit = new AccommodationUnit();
                unit.setUnitNumber(unitDto.getUnitNumber());
                unit.setActive(unitDto.isActive());
                unit.setAccommodation(accommodation);
                units.add(unit);
            }
        }
        return units;
    }
}
