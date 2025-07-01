package app.service;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import java.util.Set;

public interface AccommodationPhotoService {
    Set<AccommodationPhoto> createPhotosForAccommodation(
            Set<String> photoUrls, Accommodation accommodation);
}
