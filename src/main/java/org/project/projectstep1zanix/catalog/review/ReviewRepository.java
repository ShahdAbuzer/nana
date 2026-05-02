package org.project.projectstep1zanix.catalog.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHotelId(Long hotelId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotelId = :hotelId")
    Double findAverageRatingByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotelId = :hotelId")
    Long countByHotelId(@Param("hotelId") Long hotelId);

    boolean existsByHotelIdAndGuestId(Long hotelId, Long guestId);
}
