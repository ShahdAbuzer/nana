package org.project.projectstep1zanix.booking;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public class BookingSpecifications {

    public static Specification<Booking> hasGuestId(Long guestId) {
        return (root, query, cb) -> {
            if (guestId == null) return cb.conjunction();

            return cb.equal(root.get("guest").get("id"), guestId);
        };
    }

    public static Specification<Booking> isUpcoming(LocalDate currentDate) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("checkInDate"), currentDate);
    }

    public static Specification<Booking> isHistory(LocalDate currentDate) {
        return (root, query, cb) ->
                cb.lessThan(root.get("checkOutDate"), currentDate);
    }
}
