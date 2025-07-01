package app.service.impl;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.service.AccommodationPhotoService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationPhotoServiceImpl implements AccommodationPhotoService {

    @Override
    public Set<AccommodationPhoto> createPhotosForAccommodation(Set<String> photoUrls,
                                                                 Accommodation accommodation) {
        Set<AccommodationPhoto> photos = new HashSet<>();
        if (photoUrls != null && !photoUrls.isEmpty()) {
            for (String url : photoUrls) {
                AccommodationPhoto photo = new AccommodationPhoto();
                photo.setUrl(url);
                photo.setAccommodation(accommodation);
                photos.add(photo);
            }
        }
        return photos;
    }
}
