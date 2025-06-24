package app.mapper;

import app.config.MapperConfig;
import app.dto.accommodation.CreateAccommodationUnitDto;
import app.model.AccommodationUnit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccommodationUnitMapper {

    public AccommodationUnit toUnit(CreateAccommodationUnitDto createAccommodationUnitDto);
}
