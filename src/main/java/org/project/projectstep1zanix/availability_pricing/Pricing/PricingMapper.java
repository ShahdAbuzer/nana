package org.project.projectstep1zanix.availability_pricing.Pricing;

public class PricingMapper {

    private PricingMapper() {
    }

    public static PricingRule toEntity(PricingRuleRequestDto dto) {
        PricingRule entity = new PricingRule();
        entity.setHotelId(dto.getHotelId());
        entity.setRoomTypeId(dto.getRoomTypeId());
        entity.setName(dto.getName());
        entity.setRuleType(dto.getRuleType());
        entity.setMultiplier(dto.getMultiplier());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setActive(Boolean.TRUE.equals(dto.getActive()));
        return entity;
    }

    public static PricingRuleResponseDto toDto(PricingRule entity) {
        PricingRuleResponseDto dto = new PricingRuleResponseDto();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotelId());
        dto.setRoomTypeId(entity.getRoomTypeId());
        dto.setName(entity.getName());
        dto.setRuleType(entity.getRuleType());
        dto.setMultiplier(entity.getMultiplier());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setActive(entity.isActive());
        return dto;
    }
}