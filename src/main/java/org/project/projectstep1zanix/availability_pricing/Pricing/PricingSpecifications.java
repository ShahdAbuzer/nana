package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public final class PricingSpecifications {

    private PricingSpecifications() {
    }

    public static Specification<PricingRule> hasHotel(Long hotelId) {
        return (root, query, cb) ->
                hotelId == null ? null : cb.equal(root.get("hotelId"), hotelId);
    }

    public static Specification<PricingRule> hasRoomType(Long roomTypeId) {
        return (root, query, cb) ->
                roomTypeId == null ? null : cb.equal(root.get("roomTypeId"), roomTypeId);
    }

    public static Specification<PricingRule> hasRuleType(PricingRuleType ruleType) {
        return (root, query, cb) ->
                ruleType == null ? null : cb.equal(root.get("ruleType"), ruleType);
    }

    public static Specification<PricingRule> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<PricingRule> overlapsDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return null;
            }

            return cb.and(
                    cb.isNotNull(root.get("startDate")),
                    cb.isNotNull(root.get("endDate")),
                    cb.lessThan(root.get("startDate"), endDate),
                    cb.greaterThan(root.get("endDate"), startDate)
            );
        };
    }
}