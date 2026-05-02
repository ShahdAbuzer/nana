package org.project.projectstep1zanix.chapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    Optional<Chapter> findByBookingId(Long bookingId);

    List<Chapter> findByHotelIdAndVisibilityOrderByCreatedAtDesc(Long hotelId, ChapterVisibility visibility);

    @Query("""
            SELECT c FROM Chapter c
            JOIN Hotel h ON h.id = c.hotelId
            WHERE c.visibility = org.project.projectstep1zanix.chapter.ChapterVisibility.PUBLIC
            AND (:country IS NULL OR LOWER(h.country) = LOWER(:country))
            AND (:city IS NULL OR LOWER(h.city) = LOWER(:city))
            AND (:minRating IS NULL OR h.rating >= :minRating)
            ORDER BY c.createdAt DESC
            """)
    Page<Chapter> findPublicChapters(String country, String city, Double minRating, Pageable pageable);
}