package app.mapper.impl;

import app.dto.accommodation.CreateAccommodationUnitDto;
import app.mapper.AccommodationUnitMapper;
import app.model.AccommodationUnit;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T17:01:51+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AccommodationUnitMapperImpl implements AccommodationUnitMapper {

    @Override
    public AccommodationUnit toUnit(CreateAccommodationUnitDto createAccommodationUnitDto) {
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
}
