package org.project.projectstep1zanix.Payment;

import jakarta.validation.constraints.NotBlank;

public class PaymentFailureRequestDto {

    @NotBlank(message = "reason is required")
    private String reason;

    public PaymentFailureRequestDto() {
    }

    public PaymentFailureRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}