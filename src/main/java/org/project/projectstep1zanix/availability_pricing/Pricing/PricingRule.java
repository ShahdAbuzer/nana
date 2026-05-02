package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing_rules")
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private Long roomTypeId;

    @Column(nullable = false, length = 100)
    private String name; // e.g. Winter, Christmas, World Cup, Weekend Rule

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PricingRuleType ruleType;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplier; // e.g. 1.20 = +20%

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active = true;

    public PricingRule() {
    }

    public PricingRule(Long hotelId, Long roomTypeId, String name,
                       PricingRuleType ruleType, BigDecimal multiplier,
                       LocalDate startDate, LocalDate endDate, boolean active) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.name = name;
        this.ruleType = ruleType;
        this.multiplier = multiplier;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PricingRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(PricingRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricingRule that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}