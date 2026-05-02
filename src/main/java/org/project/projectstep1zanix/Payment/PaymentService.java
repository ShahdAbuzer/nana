package org.project.projectstep1zanix.Payment;

import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentResponseDto createPaymentIntent(PaymentRequestDto request);

    PaymentResponseDto simulateSuccess(Long paymentId);

    PaymentResponseDto simulateFailure(Long paymentId, String reason);

    PaymentResponseDto refundPayment(Long bookingId);

    PaymentResponseDto getPaymentById(Long paymentId);

    PaymentResponseDto getLatestPaymentByBookingId(Long bookingId);


    PagedResponse<PaymentResponseDto> searchPayments(
            Long bookingId,
            PaymentStatus status,
            String currency,
            String providerReference,
            Pageable pageable
    );

    Page<PaymentResponseDto> findAll(Pageable pageable);

    Payment getPaymentEntityById(Long paymentId);
    PaymentPlanResponseDto getPaymentPlanByBookingId(Long bookingId);
}