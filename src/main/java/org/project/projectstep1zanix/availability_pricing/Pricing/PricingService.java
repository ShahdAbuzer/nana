package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.time.LocalDate;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PricingService {

    PriceQuoteResponseDto getQuote(PriceQuoteRequestDto request);

    Page<PricingRuleResponseDto> findAll(Pageable pageable);

    Page<PricingRuleResponseDto> findByHotelId(Long hotelId, Pageable pageable);

    Page<PricingRuleResponseDto> findByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId, Pageable pageable);

    PricingRuleResponseDto findById(Long id);

    PricingRuleResponseDto create(PricingRuleRequestDto request);

    PricingRuleResponseDto replace(Long id, PricingRuleRequestDto request);

    void deleteById(Long id);

    PagedResponse<PricingRuleResponseDto> searchRules(
            Long hotelId,
            Long roomTypeId,
            PricingRuleType ruleType,
            Boolean active,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    PricingRule getPricingRuleEntityById(Long id);
}