package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;

public class RoomTypeResponseDto {
    private Long id;
    private Long hotelId;
    private String name;
    private String imageUrl;
    private Integer capacity;
    private Double basePrice;
    private List<String> amenities;
    private Integer totalRooms;

    public RoomTypeResponseDto() {
    }

    public RoomTypeResponseDto(Long id, Long hotelId, String name, String imageUrl,
                               Integer capacity, Double basePrice, List<String> amenities) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.capacity = capacity;
        this.basePrice = basePrice;
        this.amenities = amenities;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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