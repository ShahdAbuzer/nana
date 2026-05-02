package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, Long>,
        JpaSpecificationExecutor<Availability> {

    @Query("""
        SELECT COALESCE(SUM(a.roomsReserved), 0)
        FROM Availability a
        WHERE a.hotelId = :hotelId
          AND a.roomTypeId = :roomTypeId
          AND a.startDate < :endDate
          AND a.endDate > :startDate
          AND (:excludeId IS NULL OR a.id <> :excludeId)
          AND a.status <> :excludedStatus
    """)
    int sumReservedRooms(
            @Param("hotelId") Long hotelId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId,
            @Param("excludedStatus") AvailabilityStatus excludedStatus
    );
    List<Availability> findByBookingId(Long bookingId);
    boolean existsByHotelIdAndRoomTypeIdAndBookingIdAndStartDateAndEndDate(
        Long hotelId,
        Long roomTypeId,
        Long bookingId,
        LocalDate startDate,
        LocalDate endDate
);
}