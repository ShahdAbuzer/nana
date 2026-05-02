package org.project.projectstep1zanix.booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto dto) {
        if (dto == null) return null;

        Booking booking = new Booking();
        booking.setHotelId(dto.getHotelId());
        booking.setRoomTypeId(dto.getRoomTypeId());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());

        return booking;
    }

    public BookingResponseDto toDto(Booking entity) {
        if (entity == null) return null;

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotelId());
        dto.setRoomTypeId(entity.getRoomTypeId());
        dto.setGuestId(entity.getGuest().getId());
        dto.setCheckInDate(entity.getCheckInDate());
        dto.setCheckOutDate(entity.getCheckOutDate());
        dto.setStatus(entity.getStatus());
        dto.setTotalPrice(entity.getTotalPrice());

        return dto;
    }

    public void updateEntityFromDto(BookingRequestDto dto, Booking entity) {
        if (dto == null || entity == null) return;

        entity.setHotelId(dto.getHotelId());
        entity.setRoomTypeId(dto.getRoomTypeId());
        entity.setCheckInDate(dto.getCheckInDate());
        entity.setCheckOutDate(dto.getCheckOutDate());
    }
}