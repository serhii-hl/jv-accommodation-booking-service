package app.service.impl;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.mapper.AccommodationUnitMapper;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.service.AccommodationUnitService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationUnitServiceImpl implements AccommodationUnitService {
    private final AccommodationUnitMapper accommodationUnitMapper;

    @Override
    public List<AccommodationUnit> createUnitsForAccommodation(
            List<CreateAccommodationUnitDto> unitDtos, Accommodation accommodation) {
        List<AccommodationUnit> units = new ArrayList<>();
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
