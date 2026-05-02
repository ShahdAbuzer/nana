package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AvailabilityService {

    List<Availability> findAll();

    Availability reserve(Availability availability);

    Availability findById(Long id);

    Availability replace(Long id, Availability newAvailability);

    void deleteById(Long id);

    AvailabilityResponseDto checkAvailability(AvailabilityRequestDto dto, Long excludeAvailabilityId);

    Page<Availability> findAllPageable(Pageable pageable); 
    Page<Availability> searchAvailabilities(
            Long hotelId,
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
   
    
}