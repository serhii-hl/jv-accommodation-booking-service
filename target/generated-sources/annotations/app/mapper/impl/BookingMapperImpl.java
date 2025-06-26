package app.mapper.impl;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.mapper.BookingMapper;
import app.model.Booking;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-26T11:50:30+0300",
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
}
