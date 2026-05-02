package org.project.projectstep1zanix.catalog.Hotel;

import java.util.List;

import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
public class HotelSpecifications {

    public static Specification<Hotel> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + country.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasMinRating(Double rating) {
        return (root, query, criteriaBuilder) -> {
            if (rating == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating);
        };
    }

    public static Specification<Hotel> hasAmenities(List<String> amenities) {
        return (root, query, criteriaBuilder) -> {
            if (amenities == null || amenities.isEmpty()) {
                return null;
            }
            // For each requested amenity, the hotel must have it
            // We use a subquery-style approach: join the amenities collection
            // and check that all requested amenities are present
            query.distinct(true);
            var amenitiesJoin = root.joinList("amenities", JoinType.INNER);
            return amenitiesJoin.in(amenities);
        };
    }

    public static Specification<Hotel> hasMaxRoomPrice(Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return null;
            }
            // JOIN Hotel -> RoomType to filter by RoomType.basePrice
            query.distinct(true);
            Join<Hotel, RoomType> roomTypeJoin = root.join("roomTypes", JoinType.INNER);
            return criteriaBuilder.lessThanOrEqualTo(roomTypeJoin.get("basePrice"), maxPrice);
        };
    }
}
