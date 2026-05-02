package org.project.projectstep1zanix.Payment;

import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecifications {

    private PaymentSpecifications() {
    }

    public static Specification<Payment> hasBookingId(Long bookingId) {
        return (root, query, cb) ->
                bookingId == null ? null : cb.equal(root.get("bookingId"), bookingId);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, cb) -> {
            if (currency == null || currency.isBlank()) {
                return null;
            }
            return cb.equal(cb.upper(root.get("currency")), currency.trim().toUpperCase());
        };
    }

    public static Specification<Payment> hasProviderReference(String providerReference) {
        return (root, query, cb) -> {
            if (providerReference == null || providerReference.isBlank()) {
                return null;
            }
            return cb.like(
                    cb.upper(root.get("providerReference")),
                    "%" + providerReference.trim().toUpperCase() + "%"
            );
        };
    }
}