package org.project.projectstep1zanix.chapter;

import java.util.List;

public class ChapterRoomTypeDto {

    private Long id;
    private Long hotelId;
    private String name;
    private Integer capacity;
    private Double basePrice;
    private String imageUrl;
    private List<String> amenities;

    public Long getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}