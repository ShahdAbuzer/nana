package org.project.projectstep1zanix.catalog.Hotel;

import java.util.List;

import org.project.projectstep1zanix.catalog.nearbyplace.NearbyPlaceResponseDto;
import org.project.projectstep1zanix.catalog.review.ReviewResponseDto;

public class HotelResponseDto {

    private Long id;
    private String name;
    private String city;
    private String country;
    private String description;
    private Double rating;
    private String imageUrl;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> amenities;
    private List<NearbyPlaceResponseDto> nearbyPlaces;
    private List<ReviewResponseDto> reviews;

    public HotelResponseDto() {
    }

    public HotelResponseDto(Long id, String name, String city, String country,
                            String description, Double rating, String imageUrl,
                            String address, Double latitude, Double longitude,
                            List<String> amenities) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amenities = amenities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public List<NearbyPlaceResponseDto> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public void setNearbyPlaces(List<NearbyPlaceResponseDto> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    public List<ReviewResponseDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponseDto> reviews) {
        this.reviews = reviews;
    }
}
