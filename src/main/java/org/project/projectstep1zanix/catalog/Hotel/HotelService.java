package org.project.projectstep1zanix.catalog.Hotel;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface HotelService {

    Hotel createHotel(Hotel hotel);

    Hotel getHotelById(Long id);

    HotelPagedResponse<Hotel> findAll(String country, String city, Double minRating,
                                      Double maxPrice, List<String> amenities, Pageable pageable);

    Hotel updateHotel(Long id, Hotel hotel);

    void deleteHotel(Long id);
}
