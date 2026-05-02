package org.project.projectstep1zanix.Payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBookingId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setProviderReference(payment.getProviderReference());
        dto.setFailureReason(payment.getFailureReason());
        dto.setRefundedAmount(payment.getRefundedAmount());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}