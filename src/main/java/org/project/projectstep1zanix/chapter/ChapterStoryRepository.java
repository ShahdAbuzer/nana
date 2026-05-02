package org.project.projectstep1zanix.chapter;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterStoryRepository extends JpaRepository<ChapterStory, Long> {

    List<ChapterStory> findByHotelIdAndExpiresAtAfterOrderByCreatedAtDesc(Long hotelId, Instant now);

    List<ChapterStory> findByGuestIdAndExpiresAtAfterOrderByCreatedAtDesc(Long guestId, Instant now);

    List<ChapterStory> findByExpiresAtBefore(Instant now);
}