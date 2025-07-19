package app.mapper.impl;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.CreateAccommodationUnitDto;
import app.dto.location.LocationDto;
import app.mapper.AccommodationMapper;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.model.Amenity;
import app.model.Location;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-19T16:50:03+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AccommodationMapperImpl implements AccommodationMapper {

    @Override
    public AccommodationDto toDto(Accommodation accommodation) {
        if ( accommodation == null ) {
            return null;
        }

        AccommodationDto accommodationDto = new AccommodationDto();

        if ( accommodation.getLocation() != null ) {
            accommodationDto.setLocation( locationToLocationDto( accommodation.getLocation() ) );
        }
        if ( accommodation.getId() != null ) {
            accommodationDto.setId( accommodation.getId() );
        }
        if ( accommodation.getType() != null ) {
            accommodationDto.setType( accommodation.getType() );
        }
        if ( accommodation.getSize() != null ) {
            accommodationDto.setSize( accommodation.getSize() );
        }
        Set<Amenity> set = accommodation.getAmenitySet();
        if ( set != null ) {
            accommodationDto.setAmenitySet( new LinkedHashSet<Amenity>( set ) );
        }
        if ( accommodation.getDailyPrice() != null ) {
            accommodationDto.setDailyPrice( accommodation.getDailyPrice() );
        }

        accommodationDto.setPhotoUrls( mapPhotoUrls(accommodation.getPhotos()) );
        accommodationDto.setAvailableUnitsCount( mapUnitsAvailability(accommodation.getUnits()) );

        return accommodationDto;
    }

    @Override
    public Accommodation toEntity(CreateAccommodationDto dto) {
        if ( dto == null ) {
            return null;
        }

        Accommodation accommodation = new Accommodation();

        if ( dto.getType() != null ) {
            accommodation.setType( dto.getType() );
        }
        if ( dto.getLocation() != null ) {
            accommodation.setLocation( locationDtoToLocation( dto.getLocation() ) );
        }
        if ( dto.getSize() != null ) {
            accommodation.setSize( dto.getSize() );
        }
        Set<Amenity> set = dto.getAmenitySet();
        if ( set != null ) {
            accommodation.setAmenitySet( new LinkedHashSet<Amenity>( set ) );
        }
        if ( dto.getDailyPrice() != null ) {
            accommodation.setDailyPrice( dto.getDailyPrice() );
        }
        Set<AccommodationUnit> set1 = createAccommodationUnitDtoSetToAccommodationUnitSet( dto.getUnits() );
        if ( set1 != null ) {
            accommodation.setUnits( set1 );
        }

        return accommodation;
    }

    protected LocationDto locationToLocationDto(Location location) {
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

    protected Location locationDtoToLocation(LocationDto locationDto) {
        if ( locationDto == null ) {
            return null;
        }

        Location location = new Location();

        if ( locationDto.getCountry() != null ) {
            location.setCountry( locationDto.getCountry() );
        }
        if ( locationDto.getCity() != null ) {
            location.setCity( locationDto.getCity() );
        }
        if ( locationDto.getStreet() != null ) {
            location.setStreet( locationDto.getStreet() );
        }
        if ( locationDto.getNumber() != null ) {
            location.setNumber( locationDto.getNumber() );
        }

        return location;
    }

    protected AccommodationUnit createAccommodationUnitDtoToAccommodationUnit(CreateAccommodationUnitDto createAccommodationUnitDto) {
        if ( createAccommodationUnitDto == null ) {
            return null;
        }

        AccommodationUnit accommodationUnit = new AccommodationUnit();

        if ( createAccommodationUnitDto.getUnitNumber() != null ) {
            accommodationUnit.setUnitNumber( createAccommodationUnitDto.getUnitNumber() );
        }
        accommodationUnit.setActive( createAccommodationUnitDto.isActive() );

        return accommodationUnit;
    }

    protected Set<AccommodationUnit> createAccommodationUnitDtoSetToAccommodationUnitSet(Set<CreateAccommodationUnitDto> set) {
        if ( set == null ) {
            return null;
        }

        Set<AccommodationUnit> set1 = new LinkedHashSet<AccommodationUnit>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( CreateAccommodationUnitDto createAccommodationUnitDto : set ) {
            set1.add( createAccommodationUnitDtoToAccommodationUnit( createAccommodationUnitDto ) );
        }

        return set1;
    }
}
