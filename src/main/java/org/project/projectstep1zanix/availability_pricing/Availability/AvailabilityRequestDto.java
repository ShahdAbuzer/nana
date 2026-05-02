package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AvailabilityRequestDto {

    @NotNull(message = "hotelId is required")
    private Long hotelId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotNull(message = "startDate is required")
    private LocalDate startDate;

    @NotNull(message = "endDate is required")
    private LocalDate endDate;

    @Min(value = 1, message = "roomsRequested must be >= 1")
    private int roomsRequested = 1;

 
    @Min(value = 1, message = "guests must be >= 1")
    @NotNull(message = "guests is required")
    private Integer guests; 
    
    private Long bookingId; 
     private AvailabilityStatus status;

    public AvailabilityStatus getStatus() {
        return status;
    }


     public void setStatus(AvailabilityStatus status) {
         this.status = status;
     }


    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }


    public Long getBookingId() {
        return bookingId;
    }
    

    public AvailabilityRequestDto() {}

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

    public int getRoomsRequested() {
        return roomsRequested;
    }

    public void setRoomsRequested(int roomsRequested) {
        this.roomsRequested = roomsRequested;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }
}