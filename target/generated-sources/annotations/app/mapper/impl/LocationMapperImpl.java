package app.mapper.impl;

import app.dto.location.LocationDto;
import app.mapper.LocationMapper;
import app.model.Location;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-01T12:54:47+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class LocationMapperImpl implements LocationMapper {

    @Override
    public LocationDto toDto(Location location) {
        if ( location == null ) {
            return null;
        }

        LocationDto locationDto = new LocationDto();

        if ( location.getCountry() != null ) {
            locationDto.setCountry( location.getCountry() );
        }
        if ( location.getCity() != null ) {
            locationDto.setCity( location.getCity() );
        }
        if ( location.getStreet() != null ) {
            locationDto.setStreet( location.getStreet() );
        }
        if ( location.getNumber() != null ) {
            locationDto.setNumber( location.getNumber() );
        }

        return locationDto;
    }
}
