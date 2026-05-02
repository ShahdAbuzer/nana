package org.project.projectstep1zanix.booking;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class BookingRequestDto {

    @NotNull(message = "Hotel ID is mandatory")
    private Long hotelId;

    @NotNull(message = "Room Type ID is mandatory")
    private Long roomTypeId;

    @NotNull(message = "Check-in date is mandatory")
    @FutureOrPresent(message = "Check-in date must be in the present or future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is mandatory")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is mandatory")
    private Integer numberOfGuests;

    public BookingRequestDto() {
    }

    public BookingRequestDto(Long hotelId, Long roomTypeId,
                             LocalDate checkInDate, LocalDate checkOutDate,
                             Integer numberOfGuests) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
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

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}