package app.util;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.model.AccommodationSize;
import app.model.AccommodationType;
import app.model.AccommodationUnit;
import app.model.Amenity;
import app.model.Location;
import app.model.Role;
import app.model.User;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccommodationUtils {

    public static Accommodation createExpectedAccommodation() {
        User owner = new User();
        owner.setId(100L);
        owner.setCompanyName("Test Company");
        owner.setEmail("owner@example.com");
        owner.setFirstName("Test");
        owner.setDeleted(false);
        owner.setLastName("Owner");
        owner.setPassword("Password1234!");
        owner.setPhoneNumber("+1234567890");
        owner.setRole(Role.OWNER);
        owner.setTaxNumber("1234567890");
        owner.setTelegramChatId("123456789");
        owner.setTelegramUserId("987654321");

        Accommodation accommodation = new Accommodation();
        accommodation.setId(100L);
        accommodation.setType(AccommodationType.APARTMENT);
        accommodation.setSize(AccommodationSize.ONE_BEDROOM);
        accommodation.setDailyPrice(new BigDecimal("75.50"));
        accommodation.setOwner(owner);
        accommodation.setDeleted(false);

        Location location = new Location();
        location.setAccommodation(accommodation);
        location.setCountry("Ukraine");
        location.setCity("Kyiv");
        location.setStreet("Shevchenko");
        location.setNumber("1");
        location.setDeleted(false);
        accommodation.setLocation(location);

        AccommodationUnit unit = new AccommodationUnit();
        unit.setId(100L);
        unit.setAccommodation(accommodation);
        unit.setUnitNumber("101");
        unit.setActive(true);
        Set<AccommodationUnit> units = new HashSet<>();
        units.add(unit);
        accommodation.setUnits(units);

        Set<Amenity> amenities = new HashSet<>(Arrays.asList(
                Amenity.WIFI,
                Amenity.KITCHEN,
                Amenity.PARKING
        ));
        accommodation.setAmenitySet(amenities);

        AccommodationPhoto photo = new AccommodationPhoto();
        photo.setId(100L);
        photo.setAccommodation(accommodation);
        photo.setUrl("http://example.com/apartment_photo1.jpg");
        Set<AccommodationPhoto> photos = new HashSet<>();
        photos.add(photo);
        accommodation.setPhotos(photos);

        return accommodation;
    }
}
