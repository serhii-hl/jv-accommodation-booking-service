package app.service;

import static org.assertj.core.api.Assertions.assertThat;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.service.impl.AccommodationPhotoServiceImpl;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccommodationPhotoServiceTests {
    @InjectMocks
    private AccommodationPhotoServiceImpl accommodationPhotoService;

    @Test
    @DisplayName("Should correctly create AccommodationPhoto objects "
            + "for a given URL and link to accommodation")
    void createPhotosForAccommodationSuccess() {
        Accommodation targetAccommodation = new Accommodation();
        targetAccommodation.setId(100L);

        String photoUrl = "http://example.com/apartment_photo1.jpg";

        AccommodationPhoto expectedPhoto = new AccommodationPhoto();
        expectedPhoto.setUrl(photoUrl);
        expectedPhoto.setAccommodation(targetAccommodation);

        Set<AccommodationPhoto> expectedPhotosSet = new HashSet<>();
        expectedPhotosSet.add(expectedPhoto);
        Set<String> photoUrls = Set.of(photoUrl);

        Set<AccommodationPhoto> actualPhotos = accommodationPhotoService
                .createPhotosForAccommodation(photoUrls, targetAccommodation);

        assertThat(actualPhotos).hasSize(1);

        assertThat(actualPhotos)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(expectedPhotosSet);
        assertThat(actualPhotos.iterator().next().getAccommodation())
                .isEqualTo(targetAccommodation);
    }
}
