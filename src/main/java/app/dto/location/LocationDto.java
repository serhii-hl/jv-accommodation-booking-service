package app.dto.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private String country;
    private String city;
    private String street;
    private String number;
}
