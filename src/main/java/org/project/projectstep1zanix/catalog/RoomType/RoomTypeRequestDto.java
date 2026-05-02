package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RoomTypeRequestDto {

    @NotNull(message = "Hotel ID is mandatory")
    private Long hotelId;

    @NotBlank(message = "Room Type name is mandatory")
    private String name;

    @NotNull(message = "Capacity is mandatory")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Base price is mandatory")
    @Positive(message = "Base price must be positive")
    private Double basePrice;
    private Integer totalRooms;

    private List<String> amenities;

    public RoomTypeRequestDto() {
    }

    public RoomTypeRequestDto(Long hotelId, String name, Integer capacity, Double basePrice, List<String> amenities) {
        this.hotelId = hotelId;
        this.name = name;
        this.capacity = capacity;
        this.basePrice = basePrice;
        this.amenities = amenities;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    public Integer getTotalRooms() {
    return totalRooms;
}

public void setTotalRooms(Integer totalRooms) {
    this.totalRooms = totalRooms;
}
}