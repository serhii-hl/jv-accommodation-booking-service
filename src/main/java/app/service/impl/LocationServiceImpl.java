package app.service.impl;

import app.dto.location.LocationDto;
import app.model.Location;
import app.repository.LocationRepository;
import app.service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public Location updateLocationById(LocationDto locationDto, Long id) {
        Location location = locationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find accommodation by id " + id));
        location.setNumber(locationDto.getNumber());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());
        location.setStreet(locationDto.getStreet());
        return location;
    }

    public Location createLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setNumber(locationDto.getNumber());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());
        location.setStreet(locationDto.getStreet());
        return location;
    }
}
