package app.service;

import app.dto.location.LocationDto;
import app.model.Location;

public interface LocationService {
    Location updateLocationById(LocationDto locationDto, Long id);

    Location createLocation(LocationDto locationDto);
}
