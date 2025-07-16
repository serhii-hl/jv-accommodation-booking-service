package app.util;

import app.dto.booking.BookingDto;
import app.dto.booking.CreateBookingDto;
import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.model.AccommodationSize;
import app.model.AccommodationType;
import app.model.AccommodationUnit;
import app.model.Amenity;
import app.model.Booking;
import app.model.BookingStatus;
import app.model.Location;
import app.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BookingUtils {

    private static Accommodation createExpectedAccommodationBase(User owner) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(100L);
        accommodation.setType(AccommodationType.APARTMENT);
        accommodation.setSize(AccommodationSize.ONE_BEDROOM);
        accommodation.setDailyPrice(new BigDecimal("75.50"));
        accommodation.setOwner(owner);
        accommodation.setDeleted(false);
        return accommodation;
    }

    private static Location createExpectedLocation(Accommodation accommodation) {
        Location location = new Location();
        location.setId(accommodation.getId());
        location.setAccommodation(accommodation);
        location.setCountry("Ukraine");
        location.setCity("Kyiv");
        location.setStreet("Shevchenko");
        location.setNumber("1");
        location.setDeleted(false);
        return location;
    }

    private static AccommodationUnit createExpectedAccommodationUnit(Accommodation accommodation) {
        AccommodationUnit unit = new AccommodationUnit();
        unit.setId(100L);
        unit.setAccommodation(accommodation);
        unit.setUnitNumber("101");
        unit.setActive(true);
        return unit;
    }

    private static Set<Amenity> createExpectedAmenities() {
        return new HashSet<>(Arrays.asList(
                Amenity.WIFI,
                Amenity.KITCHEN,
                Amenity.PARKING
        ));
    }

    private static Set<AccommodationPhoto> createExpectedPhotos(Accommodation accommodation) {
        AccommodationPhoto photo = new AccommodationPhoto();
        photo.setId(100L);
        photo.setAccommodation(accommodation);
        photo.setUrl("http://example.com/apartment_photo1.jpg");
        Set<AccommodationPhoto> photos = new HashSet<>();
        photos.add(photo);
        return photos;
    }

    public static Booking createExpectedBooking() {
        User owner = UserUtils.createExpectedTestUserOwner();
        Accommodation accommodation = createExpectedAccommodationBase(owner);
        Location location = createExpectedLocation(accommodation);
        accommodation.setLocation(location);
        AccommodationUnit unit = createExpectedAccommodationUnit(accommodation);
        accommodation.setUnits(new HashSet<>(Collections.singletonList(unit)));
        accommodation.setAmenitySet(createExpectedAmenities());
        accommodation.setPhotos(createExpectedPhotos(accommodation));
        Booking booking = new Booking();
        booking.setId(100L);
        booking.setCheckInDate(LocalDate.of(2025, 8, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 8, 7));
        booking.setAccommodation(accommodation);
        booking.setUnit(unit);
        booking.setUser(owner);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setDeleted(false);
        booking.setTotalPrice(new BigDecimal("450.00"));

        return booking;
    }

    public static BookingDto createExpectedBookingDto() {
        Booking booking = createExpectedBooking();

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        if (booking.getAccommodation() != null) {
            dto.setAccommodationId(booking.getAccommodation().getId());
        }
        if (booking.getUnit() != null) {
            dto.setUnitId(booking.getUnit().getId());
        }
        dto.setStatus(booking.getStatus());
        dto.setTotalPrice(booking.getTotalPrice());

        return dto;
    }

    public static CreateBookingDto createBookingDtoToSend() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setCheckInDate(LocalDate.of(2025, 8, 1));
        dto.setCheckOutDate(LocalDate.of(2025, 8, 7));
        dto.setAccommodationId(100L);
        dto.setUnitId(100L);
        return dto;
    }
}
