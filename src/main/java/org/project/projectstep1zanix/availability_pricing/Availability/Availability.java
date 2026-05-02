package org.project.projectstep1zanix.availability_pricing.Availability;
import java.time.LocalDate;
import java.util.Objects;

import org.project.projectstep1zanix.booking.Booking;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "availability")
public class Availability {

  @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(name = "hotel_id", nullable = false)
private Long hotelId;

@Column(name = "room_type_id", nullable = false)
private Long roomTypeId;

@Column(name = "booking_id")
private Long bookingId;

@Column(nullable = false)
private LocalDate startDate;

@Column(nullable = false)
private LocalDate endDate;

@Column(nullable = false)
private int roomsReserved;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AvailabilityStatus status = AvailabilityStatus.PENDING;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "hotel_id", insertable = false, updatable = false)
private Hotel hotel;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "room_type_id", insertable = false, updatable = false)
private RoomType roomType;

@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "booking_id", insertable = false, updatable = false)
private Booking booking;

     public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Availability() {}

    public Availability(Long hotelId, Long roomTypeId, Long bookingId,
                        LocalDate startDate, LocalDate endDate,
                        int roomsReserved, AvailabilityStatus status) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.bookingId = bookingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomsReserved = roomsReserved;
        this.status = (status == null) ? AvailabilityStatus.PENDING : status;
    }


    public Long getId() { return id; }
   

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public Long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Long roomTypeId) { this.roomTypeId = roomTypeId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getRoomsReserved() { return roomsReserved; }
    public void setRoomsReserved(int roomsReserved) { this.roomsReserved = roomsReserved; }

    public AvailabilityStatus getStatus() { return status; }
    public void setStatus(AvailabilityStatus status) {
        this.status = (status == null) ? AvailabilityStatus.PENDING : status;
    }
     @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Availability))
            return false;
        Availability availability = (Availability) o;
        return Objects.equals(this.id, availability.id) && Objects.equals(this.hotelId, availability.hotelId)
                && Objects.equals(this.roomTypeId, availability.roomTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.hotelId, this.roomTypeId);
    }

    public void setId(Long id) {
        this.id = id;
    }


}