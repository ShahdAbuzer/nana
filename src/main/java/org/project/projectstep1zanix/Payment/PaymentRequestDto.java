package org.project.projectstep1zanix.Payment;

import jakarta.validation.constraints.NotNull;

public class PaymentRequestDto {

    @NotNull(message = "bookingId is required")
    private Long bookingId;

    private String currency = "USD";

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(Long bookingId, String currency) {
        this.bookingId = bookingId;
        this.currency = currency;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}