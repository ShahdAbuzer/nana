package org.project.projectstep1zanix.availability_pricing.Availability;
import java.time.LocalDate;
public class AvailabilityMapper {

    private AvailabilityMapper() {}

    public static Availability toEntity(AvailabilityRequestDto dto) {
        Availability a = new Availability();
        a.setHotelId(dto.getHotelId());
        a.setRoomTypeId(dto.getRoomTypeId());
        a.setStartDate(dto.getStartDate());
        a.setEndDate(dto.getEndDate());
        a.setRoomsReserved(dto.getRoomsRequested());
        a.setBookingId(dto.getBookingId());
        a.setStatus(dto.getStatus());        
        return a;
    }

    public static AvailabilityDetailsDto toDetailsDto(Availability entity) {
        AvailabilityDetailsDto dto = new AvailabilityDetailsDto();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotelId());
        dto.setRoomTypeId(entity.getRoomTypeId());
        dto.setBookingId(entity.getBookingId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setRoomsReserved(entity.getRoomsReserved());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public static AvailabilityResponseDto fromAvailabilityCheck(
            boolean available,
            int remainingRooms,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new AvailabilityResponseDto(
                available,
                remainingRooms,
                startDate,
                endDate
        );
    }
}