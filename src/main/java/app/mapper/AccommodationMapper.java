package app.mapper;

import app.config.MapperConfig;
import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.model.AccommodationUnit;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccommodationMapper {
    @Mapping(target = "location", source = "location")
    @Mapping(target = "photoUrls", expression = "java(mapPhotoUrls(accommodation.getPhotos()))")
    @Mapping(target = "availableUnitsCount",
            expression = "java(mapUnitsAvailability(accommodation.getUnits()))")
    AccommodationDto toDto(Accommodation accommodation);

    default Set<String> mapPhotoUrls(Set<AccommodationPhoto> photos) {
        if (photos == null) {
            return Collections.emptySet();
        }
        return photos.stream()
                .map(AccommodationPhoto::getUrl)
                .collect(Collectors.toSet());
    }

    default Integer mapUnitsAvailability(Set<AccommodationUnit> units) {
        if (units == null) {
            return 0;
        }
        return (int) units.stream()
                .filter(AccommodationUnit::isActive)
                .count();
    }

    Accommodation toEntity(CreateAccommodationDto dto);
}
