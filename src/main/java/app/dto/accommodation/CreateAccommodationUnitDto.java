package app.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccommodationUnitDto {
    @NotBlank(message = "Unit number cannot be blank")
    private String unitNumber;
    private boolean isActive = true;
}
