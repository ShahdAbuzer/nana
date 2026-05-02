package org.project.projectstep1zanix.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentResponseDto {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String providerReference;
    private String failureReason;
    private BigDecimal refundedAmount;
    private Instant createdAt;
    private Instant updatedAt;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long id, Long bookingId, BigDecimal amount, String currency,
                              PaymentStatus status, String providerReference, String failureReason,
                              BigDecimal refundedAmount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.providerReference = providerReference;
        this.failureReason = failureReason;
        this.refundedAmount = refundedAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}