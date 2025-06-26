package app.mapper;

import app.config.MapperConfig;
import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    BookingDto toDto(Booking booking);

    Booking toBooking(CreateBookingDto createBookingDto);
}
