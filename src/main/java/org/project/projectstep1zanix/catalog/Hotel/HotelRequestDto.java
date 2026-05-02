package org.project.projectstep1zanix.catalog.Hotel;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HotelRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;

    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> amenities;

    public HotelRequestDto() {
    }

    public HotelRequestDto(String name, String city, String country, String description,
                           Double rating, String address, Double latitude, Double longitude,
                           List<String> amenities) {
        this.name = name;
        this.city = city;
        this.country = country;
        this.description = description;
        this.rating = rating;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amenities = amenities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}
