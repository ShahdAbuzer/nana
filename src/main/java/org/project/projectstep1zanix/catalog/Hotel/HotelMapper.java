package org.project.projectstep1zanix.catalog.Hotel;

import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequestDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());
        hotel.setAddress(dto.getAddress());
        hotel.setLatitude(dto.getLatitude());
        hotel.setLongitude(dto.getLongitude());
        hotel.setAmenities(dto.getAmenities());
        return hotel;
    }

    public HotelResponseDto toResponseDto(Hotel hotel) {
        HotelResponseDto dto = new HotelResponseDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setCountry(hotel.getCountry());
        dto.setDescription(hotel.getDescription());
        dto.setRating(hotel.getRating());
        dto.setImageUrl(hotel.getImageUrl());
        dto.setAddress(hotel.getAddress());
        dto.setLatitude(hotel.getLatitude());
        dto.setLongitude(hotel.getLongitude());
        dto.setAmenities(hotel.getAmenities());
        return dto;
    }

    public void updateEntityFromDto(HotelRequestDto dto, Hotel hotel) {
        hotel.setName(dto.getName());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());
        hotel.setAddress(dto.getAddress());
        hotel.setLatitude(dto.getLatitude());
        hotel.setLongitude(dto.getLongitude());
        hotel.setAmenities(dto.getAmenities());
    }
}
