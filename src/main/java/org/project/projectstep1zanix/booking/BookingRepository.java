package org.project.projectstep1zanix.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findByGuest_Id(Long guestId);

    boolean existsByGuest_IdAndHotelIdAndStatus(
            Long guestId,
            Long hotelId,
            BookingStatus status
    );

    @Query("""
           SELECT b FROM Booking b
           WHERE b.roomTypeId = :roomTypeId
           AND b.status IN (
                org.project.projectstep1zanix.booking.BookingStatus.PENDING,
                org.project.projectstep1zanix.booking.BookingStatus.CONFIRMED
           )
           AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)
           """)
    List<Booking> findOverlappingBookings(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}