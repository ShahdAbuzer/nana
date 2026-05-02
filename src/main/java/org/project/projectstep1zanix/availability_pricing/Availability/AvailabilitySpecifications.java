package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public final class AvailabilitySpecifications {

    private AvailabilitySpecifications() {
    }

    public static Specification<Availability> hasHotel(Long hotelId) {
        return (root, query, cb) ->
                hotelId == null ? null : cb.equal(root.get("hotelId"), hotelId);
    }

    public static Specification<Availability> hasRoomType(Long roomTypeId) {
        return (root, query, cb) ->
                roomTypeId == null ? null : cb.equal(root.get("roomTypeId"), roomTypeId);
    }

    public static Specification<Availability> overlaps(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null) {
                return null;
            }
            return cb.and(
                    cb.lessThan(root.get("startDate"), end),
                    cb.greaterThan(root.get("endDate"), start)
            );
        };
    }

    public static Specification<Availability> excludeAvailabilityId(Long availabilityId) {
        return (root, query, cb) ->
                availabilityId == null ? null : cb.notEqual(root.get("id"), availabilityId);
    }
}