package app.specification;

import app.model.Booking;
import app.model.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

public class BookingSpecification {
    public static Specification<Booking> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ? null :
              criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Booking> hasAccommodationId(Long accommodationId) {
        return (root, query, criteriaBuilder) ->
                accommodationId == null ? null :
                    criteriaBuilder.equal(root.get("accommodation").get("id"), accommodationId);
    }

    public static Specification<Booking> hasAccommodationOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) ->
                ownerId == null ? null :
                        criteriaBuilder.equal(root.get("accommodation")
                                .get("owner").get("id"), ownerId);
    }

    public static Specification<Booking> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }
}
