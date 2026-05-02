package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long>, JpaSpecificationExecutor<RoomType> {
    List<RoomType> findByHotelId(Long hotelId);
    Optional<RoomType> findByIdAndHotelId(Long id, Long hotelId);
}
