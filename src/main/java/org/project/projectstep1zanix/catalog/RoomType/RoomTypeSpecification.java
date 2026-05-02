package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public class RoomTypeSpecification {

    public static Specification<RoomType> hasHotelId(Long hotelId) {
        return (root, query, cb) -> {
            if (hotelId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("hotelId"), hotelId);
        };
    }

    public static Specification<RoomType> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<RoomType> hasCapacity(Integer capacity) {
        return (root, query, cb) -> {
            if (capacity == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("capacity"), capacity);
        };
    }

    public static Specification<RoomType> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
        };
    }

    public static Specification<RoomType> hasMinPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
        };
    }

    public static Specification<RoomType> hasAmenities(List<String> amenities) {
        return (root, query, cb) -> {
            if (amenities == null || amenities.isEmpty()) {
                return cb.conjunction();
            }
            query.distinct(true);
            var amenitiesJoin = root.joinList("amenities", JoinType.INNER);
            return amenitiesJoin.in(amenities);
        };
    }
}
