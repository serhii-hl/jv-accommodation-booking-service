package app.dto.accommodation;

import app.dto.location.LocationDto;
import app.model.AccommodationSize;
import app.model.AccommodationType;
import app.model.Amenity;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccommodationDto {
    private AccommodationType type;
    private LocationDto location;
    private AccommodationSize size;
    private Set<Amenity> amenitySet;
    @Positive
    private BigDecimal dailyPrice;
}
