package app.mapper;

import app.config.MapperConfig;
import app.dto.location.LocationDto;
import app.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
    LocationDto toDto(Location location);
}
