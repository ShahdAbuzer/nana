package org.project.projectstep1zanix.catalog.RoomType;
import java.util.ArrayList;
import java.util.List;

import org.project.projectstep1zanix.catalog.Hotel.Hotel;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_types")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer capacity;
    private Double basePrice;
    private String imageUrl;

    @ElementCollection
    @CollectionTable(
            name = "room_type_amenities",
            joinColumns = @JoinColumn(name = "room_type_id")
    )
    @Column(name = "amenity", nullable = false)
    private List<String> amenities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "hotel_id", insertable = false, updatable = false)
    private Long hotelId;
    @Column(nullable = false)
    private Integer totalRooms;

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public RoomType() {
    }

    public RoomType(Long id, String name, Integer capacity, Double basePrice, String imageUrl,
                    List<String> amenities, Hotel hotel, Long hotelId, Integer totalRooms) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.hotel = hotel;
        this.hotelId = hotelId;
        this.totalRooms = totalRooms;
    }

    public Long getId() {
        return id;
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

    public Hotel getHotel() {
        return hotel;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
    
    
}