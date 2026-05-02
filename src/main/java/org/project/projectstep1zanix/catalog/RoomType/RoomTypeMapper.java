package org.project.projectstep1zanix.catalog.RoomType;

import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeMapper {

    public RoomType toEntity(RoomTypeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        RoomType roomType = new RoomType();
        roomType.setName(dto.getName());
        roomType.setCapacity(dto.getCapacity());
        roomType.setBasePrice(dto.getBasePrice());
        roomType.setAmenities(dto.getAmenities());
        roomType.setTotalRooms(dto.getTotalRooms());

        if (dto.getHotelId() != null) {
            Hotel hotel = new Hotel();
            hotel.setId(dto.getHotelId());
            roomType.setHotel(hotel);
        }

        return roomType;
    }

    public RoomTypeResponseDto toDto(RoomType entity) {
        if (entity == null) {
            return null;
        }

        RoomTypeResponseDto dto = new RoomTypeResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCapacity(entity.getCapacity());
        dto.setBasePrice(entity.getBasePrice());
        dto.setAmenities(entity.getAmenities());
        dto.setImageUrl(entity.getImageUrl());
        dto.setTotalRooms(entity.getTotalRooms());
        dto.setHotelId(entity.getHotel() != null ? entity.getHotel().getId() : null);

        return dto;
    }

    public void updateEntityFromDto(RoomTypeRequestDto dto, RoomType entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setCapacity(dto.getCapacity());
        entity.setBasePrice(dto.getBasePrice());
        entity.setAmenities(dto.getAmenities());
        entity.setTotalRooms(dto.getTotalRooms());

        if (dto.getHotelId() != null) {
            Hotel hotel = new Hotel();
            hotel.setId(dto.getHotelId());
            entity.setHotel(hotel);
        }
    }
}