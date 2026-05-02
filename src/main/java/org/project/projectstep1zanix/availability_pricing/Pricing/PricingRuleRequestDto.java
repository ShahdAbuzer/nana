package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PricingRuleRequestDto {

    @NotNull(message = "hotelId is required")
    private Long hotelId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "ruleType is required")
    private PricingRuleType ruleType;

    @NotNull(message = "multiplier is required")
    @DecimalMin(value = "0.01", message = "multiplier must be greater than 0")
    private BigDecimal multiplier;
    
    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "active is required")
    private Boolean active;

    public PricingRuleRequestDto() {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}