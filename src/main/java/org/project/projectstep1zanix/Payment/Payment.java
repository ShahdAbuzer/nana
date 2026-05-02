package org.project.projectstep1zanix.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 100, unique = true)
    private String providerReference;

    @Column(length = 255)
    private String failureReason;

    @Column(precision = 12, scale = 2)
    private BigDecimal refundedAmount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Payment() {
    }

    public Payment(Long id, Long bookingId, BigDecimal amount, String currency, PaymentStatus status,
                   String providerReference, String failureReason, BigDecimal refundedAmount,
                   Instant createdAt, Instant updatedAt) {
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

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
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
@Override
public boolean equals(Object o) {
    if (this == o)
        return true;
    if (!(o instanceof Payment))
        return false;
    Payment payment = (Payment) o;
    return Objects.equals(this.id, payment.id)
            && Objects.equals(this.bookingId, payment.bookingId)
            && Objects.equals(this.providerReference, payment.providerReference);
}

@Override
public int hashCode() {
    return Objects.hash(this.id, this.bookingId, this.providerReference);
}



}