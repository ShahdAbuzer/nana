package org.project.projectstep1zanix.catalog.nearbyplace;

public class NearbyPlaceResponseDto {

    private Long id;
    private Long hotelId;
    private String name;
    private String type;
    private Double distanceKm;
    private String description;
    private Double latitude;
    private Double longitude;

    public NearbyPlaceResponseDto() {
    }

    public NearbyPlaceResponseDto(Long id, Long hotelId, String name, String type,
                                  Double distanceKm, String description,
                                  Double latitude, Double longitude) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.type = type;
        this.distanceKm = distanceKm;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
