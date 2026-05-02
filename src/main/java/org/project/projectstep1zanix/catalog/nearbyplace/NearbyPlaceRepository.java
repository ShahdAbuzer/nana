package org.project.projectstep1zanix.catalog.nearbyplace;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NearbyPlaceRepository extends JpaRepository<NearbyPlace, Long> {
    List<NearbyPlace> findByHotelId(Long hotelId);
}
