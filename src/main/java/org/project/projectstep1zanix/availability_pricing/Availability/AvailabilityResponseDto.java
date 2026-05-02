package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;

public class AvailabilityResponseDto {

    private boolean available;
    private int remainingRooms;
    private LocalDate startDate;
    private LocalDate endDate;

    public AvailabilityResponseDto() {
    }

    public AvailabilityResponseDto(boolean available, int remainingRooms,
                                   LocalDate startDate, LocalDate endDate) {
        this.available = available;
        this.remainingRooms = remainingRooms;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getRemainingRooms() {
        return remainingRooms;
    }

    public void setRemainingRooms(int remainingRooms) {
        this.remainingRooms = remainingRooms;
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
}