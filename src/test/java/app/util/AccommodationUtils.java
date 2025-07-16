package app.util;

import app.dto.accommodation.AccommodationDto;
import app.dto.accommodation.CreateAccommodationDto;
import app.dto.accommodation.CreateAccommodationUnitDto;
import app.dto.accommodation.UpdateAccommodationDto;
import app.dto.location.LocationDto;
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
import java.util.stream.Collectors;

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

    public static LocationDto createExpectedLocationDto() {
        LocationDto locationDto = new LocationDto();
        locationDto.setCountry("Ukraine");
        locationDto.setCity("Kyiv");
        locationDto.setStreet("Shevchenko");
        locationDto.setNumber("1");
        return locationDto;
    }

    public static AccommodationDto createExpectedAccommodationDto() {
        Accommodation accommodation = createExpectedAccommodation();

        AccommodationDto dto = new AccommodationDto();
        dto.setId(accommodation.getId());
        dto.setType(accommodation.getType());
        dto.setLocation(createExpectedLocationDto());
        dto.setSize(accommodation.getSize());
        dto.setAmenitySet(accommodation.getAmenitySet());
        dto.setDailyPrice(accommodation.getDailyPrice());

        Set<String> photoUrls = accommodation.getPhotos().stream()
                .map(AccommodationPhoto::getUrl)
                .collect(Collectors.toSet());
        dto.setPhotoUrls(photoUrls);

        long availableUnits = accommodation.getUnits().stream()
                .filter(AccommodationUnit::isActive)
                .count();
        dto.setAvailableUnitsCount((int) availableUnits);

        return dto;
    }

    public static Location createExpectedLocation() {
        Location location = new Location();
        location.setCountry("Ukraine");
        location.setCity("Kyiv");
        location.setStreet("Shevchenko");
        location.setNumber("1");
        location.setDeleted(false);
        return location;
    }

    public static CreateAccommodationDto createExpectedCreateAccommodationDto() {
        CreateAccommodationDto dto = new CreateAccommodationDto();

        dto.setType(AccommodationType.APARTMENT);
        dto.setSize(AccommodationSize.ONE_BEDROOM);
        dto.setDailyPrice(new BigDecimal("75.50"));

        LocationDto locationDto = new LocationDto();
        locationDto.setCountry("Ukraine");
        locationDto.setCity("Kyiv");
        locationDto.setStreet("Shevchenko");
        locationDto.setNumber("1");
        dto.setLocation(locationDto);

        Set<Amenity> amenities = new HashSet<>(Arrays.asList(
                Amenity.WIFI,
                Amenity.KITCHEN,
                Amenity.PARKING
        ));
        dto.setAmenitySet(amenities);

        Set<String> photoUrls = new HashSet<>();
        photoUrls.add("http://example.com/apartment_photo1.jpg");
        dto.setInitialPhotoUrls(photoUrls);

        CreateAccommodationUnitDto unitDto = new CreateAccommodationUnitDto();
        unitDto.setUnitNumber("101");
        unitDto.setActive(true);

        Set<CreateAccommodationUnitDto> units = new HashSet<>();
        units.add(unitDto);
        dto.setUnits(units);

        return dto;
    }

    public static UpdateAccommodationDto createExpectedUpdateAccommodationDto() {
        UpdateAccommodationDto dto = new UpdateAccommodationDto();
        dto.setType(AccommodationType.APARTMENT);
        dto.setSize(AccommodationSize.ONE_BEDROOM);
        dto.setDailyPrice(new BigDecimal("75.50"));
        dto.setLocation(createExpectedLocationDto());

        Set<Amenity> amenities = new HashSet<>(Arrays.asList(
                Amenity.WIFI,
                Amenity.KITCHEN,
                Amenity.PARKING
        ));
        dto.setAmenitySet(amenities);

        return dto;
    }
}
