package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;

public class AvailabilityDetailsDto {

    private Long id;
    private Long hotelId;
    private Long roomTypeId;
    private Long bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int roomsReserved;
    private AvailabilityStatus status;

    public AvailabilityDetailsDto() {}

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

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public int getRoomsReserved() {
        return roomsReserved;
    }

    public void setRoomsReserved(int roomsReserved) {
        this.roomsReserved = roomsReserved;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }
}