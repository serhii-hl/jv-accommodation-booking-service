package app.dto.accommodation;

import app.dto.location.LocationDto;
import app.model.AccommodationSize;
import app.model.AccommodationType;
import app.model.Amenity;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccommodationDto {
    private Long id;
    private AccommodationType type;
    private LocationDto location;
    private AccommodationSize size;
    private Set<Amenity> amenitySet;
    private BigDecimal dailyPrice;
    private Set<String> photoUrls;
    private Integer availableUnitsCount;
}
