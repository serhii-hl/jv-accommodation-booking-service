package app.dto.accommodation;

import app.dto.location.LocationDto;
import app.model.AccommodationSize;
import app.model.AccommodationType;
import app.model.Amenity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccommodationDto {
    @NotNull(message = "Type cannot be null")
    private AccommodationType type;
    @Valid
    @NotNull(message = "Location cannot be null")
    private LocationDto location;
    @NotNull(message = "Size cannot be null")
    private AccommodationSize size;
    private Set<Amenity> amenitySet;
    @NotNull(message = "Daily price cannot be null")
    @Positive(message = "Daily price must be positive")
    private BigDecimal dailyPrice;
    private Set<String> initialPhotoUrls;
    @Valid
    @NotNull(message = "Units list cannot be null")
    @Size(min = 1, message = "Accommodation must have at least one unit")
    private Set<CreateAccommodationUnitDto> units;
}
