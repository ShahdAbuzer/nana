package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long>, JpaSpecificationExecutor<PricingRule> {

    List<PricingRule> findByHotelId(Long hotelId);
    List<PricingRule> findByRoomTypeId(Long roomTypeId);
    List<PricingRule> findByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId);
      List<PricingRule> findByHotelIdAndRoomTypeIdAndActiveTrue(Long hotelId, Long roomTypeId);
      boolean existsByHotelIdAndRoomTypeIdAndNameAndRuleTypeAndStartDateAndEndDate(
        Long hotelId,
        Long roomTypeId,
        String name,
        PricingRuleType ruleType,
        LocalDate startDate,
        LocalDate endDate
);

}