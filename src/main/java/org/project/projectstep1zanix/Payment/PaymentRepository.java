package org.project.projectstep1zanix.Payment;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findTopByBookingIdOrderByCreatedAtDesc(Long bookingId);

    boolean existsByBookingIdAndStatusIn(Long bookingId, Collection<PaymentStatus> statuses);
    Optional<Payment> findTopByBookingIdAndStatusOrderByCreatedAtDesc(Long bookingId, PaymentStatus status);
}