package app.mapper.impl;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.mapper.BookingMapper;
import app.model.Accommodation;
import app.model.AccommodationUnit;
import app.model.Booking;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-19T16:50:03+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingDto toDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();

        Long id = bookingAccommodationId( booking );
        if ( id != null ) {
            bookingDto.setAccommodationId( id );
        }
        Long id1 = bookingUnitId( booking );
        if ( id1 != null ) {
            bookingDto.setUnitId( id1 );
        }
        if ( booking.getId() != null ) {
            bookingDto.setId( booking.getId() );
        }
        if ( booking.getCheckInDate() != null ) {
            bookingDto.setCheckInDate( booking.getCheckInDate() );
        }
        if ( booking.getCheckOutDate() != null ) {
            bookingDto.setCheckOutDate( booking.getCheckOutDate() );
        }
        if ( booking.getStatus() != null ) {
            bookingDto.setStatus( booking.getStatus() );
        }
        if ( booking.getTotalPrice() != null ) {
            bookingDto.setTotalPrice( booking.getTotalPrice() );
        }

        return bookingDto;
    }

    @Override
    public Booking toBooking(CreateBookingDto createBookingDto) {
        if ( createBookingDto == null ) {
            return null;
        }

        Booking booking = new Booking();

        if ( createBookingDto.getCheckInDate() != null ) {
            booking.setCheckInDate( createBookingDto.getCheckInDate() );
        }
        if ( createBookingDto.getCheckOutDate() != null ) {
            booking.setCheckOutDate( createBookingDto.getCheckOutDate() );
        }

        return booking;
    }

    private Long bookingAccommodationId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Accommodation accommodation = booking.getAccommodation();
        if ( accommodation == null ) {
            return null;
        }
        Long id = accommodation.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long bookingUnitId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        AccommodationUnit unit = booking.getUnit();
        if ( unit == null ) {
            return null;
        }
        Long id = unit.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
