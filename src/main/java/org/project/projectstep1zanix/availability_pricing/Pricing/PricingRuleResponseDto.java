package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PricingRuleResponseDto {

    private Long id;
    private Long hotelId;
    private Long roomTypeId;
    private String name;
    private PricingRuleType ruleType;
    private BigDecimal multiplier;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public PricingRuleResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}