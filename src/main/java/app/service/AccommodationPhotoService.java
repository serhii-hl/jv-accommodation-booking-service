package app.service;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import java.util.List;

public interface AccommodationPhotoService {
    List<AccommodationPhoto> createPhotosForAccommodation(
            List<String> photoUrls, Accommodation accommodation);
}
