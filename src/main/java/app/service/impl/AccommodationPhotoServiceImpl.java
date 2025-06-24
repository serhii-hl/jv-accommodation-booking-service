package app.service.impl;

import app.model.Accommodation;
import app.model.AccommodationPhoto;
import app.service.AccommodationPhotoService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationPhotoServiceImpl implements AccommodationPhotoService {

    @Override
    public List<AccommodationPhoto> createPhotosForAccommodation(List<String> photoUrls,
                                                                 Accommodation accommodation) {
        List<AccommodationPhoto> photos = new ArrayList<>();
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
