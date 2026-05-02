package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PricingServiceImpl implements PricingService {

   private final PricingRuleRepository pricingRuleRepository;
private final RoomTypeRepository roomTypeRepository;

public PricingServiceImpl(PricingRuleRepository pricingRuleRepository,
                          RoomTypeRepository roomTypeRepository) {
    this.pricingRuleRepository = pricingRuleRepository;
    this.roomTypeRepository = roomTypeRepository;
}
  private PricingRule findBestDayTypeRule(List<PricingRule> rules, LocalDate date) {
    PricingRule best = null;

    for (PricingRule rule : rules) {
        if (!appliesToDate(rule, date)) {
            continue;
        }

        if (rule.getRuleType() != PricingRuleType.WEEKDAY &&
            rule.getRuleType() != PricingRuleType.WEEKEND) {
            continue;
        }

        if (best == null || rule.getMultiplier().compareTo(best.getMultiplier()) > 0) {
            best = rule;
        }
    }

    return best;
}

private PricingRule findBestSeasonalRule(List<PricingRule> rules, LocalDate date) {
    PricingRule best = null;

    for (PricingRule rule : rules) {
        if (!appliesToDate(rule, date)) {
            continue;
        }

        if (rule.getRuleType() != PricingRuleType.SEASONAL) {
            continue;
        }

        if (best == null || rule.getMultiplier().compareTo(best.getMultiplier()) > 0) {
            best = rule;
        }
    }

    return best;
}

private PricingRule findBestHolidayOrEventRule(List<PricingRule> rules, LocalDate date) {
    PricingRule best = null;

    for (PricingRule rule : rules) {
        if (!appliesToDate(rule, date)) {
            continue;
        }

        if (rule.getRuleType() != PricingRuleType.HOLIDAY &&
            rule.getRuleType() != PricingRuleType.EVENT) {
            continue;
        }

        if (best == null || rule.getMultiplier().compareTo(best.getMultiplier()) > 0) {
            best = rule;
        }
    }

    return best;
}

private PricingRule findWinningRule(List<PricingRule> rules, LocalDate date) {
    PricingRule holidayOrEventRule = findBestHolidayOrEventRule(rules, date);
    if (holidayOrEventRule != null) {
        return holidayOrEventRule;
    }

    PricingRule seasonalRule = findBestSeasonalRule(rules, date);
    if (seasonalRule != null) {
        return seasonalRule;
    }

    return findBestDayTypeRule(rules, date);
}
   @Override
@Transactional(readOnly = true)
public PriceQuoteResponseDto getQuote(PriceQuoteRequestDto request) {
   validateQuoteRequest(request);
validateDateRange(request.getStartDate(), request.getEndDate());
validateGuestCapacity(
        request.getHotelId(),
        request.getRoomTypeId(),
        request.getGuests()
);

BigDecimal basePricePerNight = resolveBasePriceForRoomType(
        request.getHotelId(),
        request.getRoomTypeId()
);
    String currency = "USD";

    List<PricingRule> activeRules = pricingRuleRepository
            .findByHotelIdAndRoomTypeIdAndActiveTrue(
                    request.getHotelId(),
                    request.getRoomTypeId()
            );

    BigDecimal totalPrice = BigDecimal.ZERO;
    List<String> appliedRules = new ArrayList<>();

    for (LocalDate date = request.getStartDate(); date.isBefore(request.getEndDate()); date = date.plusDays(1)) {
        BigDecimal nightlyPrice = basePricePerNight;

        PricingRule winningRule = findWinningRule(activeRules, date);

        if (winningRule != null) {
            nightlyPrice = nightlyPrice.multiply(winningRule.getMultiplier());

            String label = buildRuleLabel(winningRule);
            if (!appliedRules.contains(label)) {
                appliedRules.add(label);
            }
        }

        totalPrice = totalPrice.add(nightlyPrice);
    }

    totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

    PriceQuoteResponseDto response = new PriceQuoteResponseDto();
    response.setStartDate(request.getStartDate());
    response.setEndDate(request.getEndDate());
    response.setNights((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()));
    response.setBasePricePerNight(basePricePerNight);
    response.setAppliedRules(appliedRules);
    response.setTotalPrice(totalPrice);
    response.setCurrency(currency);

    return response;
}
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PricingRuleResponseDto> searchRules(
            Long hotelId,
            Long roomTypeId,
            PricingRuleType ruleType,
            Boolean active,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<PricingRule> spec = Specification
                .where(PricingSpecifications.hasHotel(hotelId))
                .and(PricingSpecifications.hasRoomType(roomTypeId))
                .and(PricingSpecifications.hasRuleType(ruleType))
                .and(PricingSpecifications.isActive(active))
                .and(PricingSpecifications.overlapsDateRange(startDate, endDate));

        Page<PricingRuleResponseDto> page = pricingRuleRepository.findAll(spec, pageable)
                .map(PricingMapper::toDto);

        return PagedResponse.from(page, page.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public PricingRuleResponseDto findById(Long id) {
        PricingRule entity = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new PricingRuleNotFoundException("Pricing rule not found with id: " + id));

        return PricingMapper.toDto(entity);
    }

    @Override
public PricingRuleResponseDto create(PricingRuleRequestDto request) {
    validatePricingRuleRequest(request);
    validateRuleDates(request);

    boolean exists = pricingRuleRepository
            .existsByHotelIdAndRoomTypeIdAndNameAndRuleTypeAndStartDateAndEndDate(
                    request.getHotelId(),
                    request.getRoomTypeId(),
                    request.getName(),
                    request.getRuleType(),
                    request.getStartDate(),
                    request.getEndDate()
            );

    if (exists) {
        throw new PricingRuleConflictException("Pricing rule already exists.");
    }

    PricingRule entity = PricingMapper.toEntity(request);
    PricingRule saved = pricingRuleRepository.save(entity);

    return PricingMapper.toDto(saved);
}
    @Override
    public PricingRuleResponseDto replace(Long id, PricingRuleRequestDto request) {
        validatePricingRuleRequest(request);
        validateRuleDates(request);

        PricingRule existing = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new PricingRuleNotFoundException("Pricing rule not found with id: " + id));

        existing.setHotelId(request.getHotelId());
        existing.setRoomTypeId(request.getRoomTypeId());
        existing.setName(request.getName());
        existing.setRuleType(request.getRuleType());
        existing.setMultiplier(request.getMultiplier());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setActive(Boolean.TRUE.equals(request.getActive()));

        PricingRule updated = pricingRuleRepository.save(existing);

        return PricingMapper.toDto(updated);
    }

    private RoomType getRoomType(Long hotelId, Long roomTypeId) {
    return roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
            .orElseThrow(() -> new PricingRuleNotFoundException(
                    "Room type " + roomTypeId + " not found for hotel " + hotelId
            ));
}
private void validateGuestCapacity(Long hotelId, Long roomTypeId, Integer guests) {
    RoomType roomType = getRoomType(hotelId, roomTypeId);

    if (roomType.getCapacity() == null || roomType.getCapacity() <= 0) {
        throw new InvalidPricingRuleException("Room capacity is not configured.");
    }

    if (guests > roomType.getCapacity()) {
        throw new InvalidPricingRuleException(
                "This room type allows a maximum of " + roomType.getCapacity() + " guest(s)."
        );
    }
}

    @Override
    public void deleteById(Long id) {
        if (!pricingRuleRepository.existsById(id)) {
            throw new PricingRuleNotFoundException("Pricing rule not found with id: " + id);
        }

        pricingRuleRepository.deleteById(id);
    }

    private void validateQuoteRequest(PriceQuoteRequestDto request) {
        if (request == null) {
            throw new InvalidPricingRuleException("Quote request must not be null.");
        }
        if (request.getHotelId() == null) {
            throw new InvalidPricingRuleException("Hotel ID must not be null.");
        }
        if (request.getRoomTypeId() == null) {
            throw new InvalidPricingRuleException("Room type ID must not be null.");
        }
        if (request.getGuests() == null || request.getGuests() <= 0) {
            throw new InvalidPricingRuleException("Guests must be greater than 0.");
        }
    }

    private void validatePricingRuleRequest(PricingRuleRequestDto request) {
        if (request == null) {
            throw new InvalidPricingRuleException("Pricing rule request must not be null.");
        }
        if (request.getHotelId() == null) {
            throw new InvalidPricingRuleException("Hotel ID must not be null.");
        }
        if (request.getRoomTypeId() == null) {
            throw new InvalidPricingRuleException("Room type ID must not be null.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidPricingRuleException("Rule name must not be blank.");
        }
        if (request.getRuleType() == null) {
            throw new InvalidPricingRuleException("Rule type must not be null.");
        }
        if (request.getMultiplier() == null || request.getMultiplier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPricingRuleException("Multiplier must be greater than 0.");
        }
        if (request.getActive() == null) {
            throw new InvalidPricingRuleException("Active flag must not be null.");
        }
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new InvalidPricingRuleException("Start date and end date must not be null.");
        }
        if (!end.isAfter(start)) {
            throw new InvalidPricingRuleException("End date must be after start date.");
        }
    }

    private void validateRuleDates(PricingRuleRequestDto request) {
        PricingRuleType type = request.getRuleType();

        if (type == PricingRuleType.SEASONAL
                || type == PricingRuleType.HOLIDAY
                || type == PricingRuleType.EVENT) {

            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new InvalidPricingRuleException("Start date and end date are required for " + type + " rules.");
            }

            if (!request.getEndDate().isAfter(request.getStartDate())) {
                throw new InvalidPricingRuleException("End date must be after start date.");
            }
        }
    }

    private boolean appliesToDate(PricingRule rule, LocalDate date) {
        if (!rule.isActive()) {
            return false;
        }

        return switch (rule.getRuleType()) {
            case WEEKDAY -> isWeekday(date);
            case WEEKEND -> isWeekend(date);
            case SEASONAL, HOLIDAY, EVENT -> isWithinRange(date, rule.getStartDate(), rule.getEndDate());
        };
    }

    private boolean isWeekday(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY;
    }

    private boolean isWithinRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        return !date.isBefore(startDate) && date.isBefore(endDate);
    }

    private String buildRuleLabel(PricingRule rule) {
        if (rule.getName() != null && !rule.getName().isBlank()) {
            return rule.getRuleType() + " (" + rule.getName() + ") x" + rule.getMultiplier();
        }
        return rule.getRuleType() + " x" + rule.getMultiplier();
    }
    
private BigDecimal resolveBasePriceForRoomType(Long hotelId, Long roomTypeId) {
    RoomType roomType = roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
            .orElseThrow(() -> new PricingRuleNotFoundException(
                    "Room type " + roomTypeId + " not found for hotel " + hotelId
            ));

    if (roomType.getBasePrice() == null || roomType.getBasePrice() <= 0) {
        throw new PricingRuleNotFoundException(
                "Base price not configured for room type ID: " + roomTypeId
        );
    }

    return BigDecimal.valueOf(roomType.getBasePrice()).setScale(2, RoundingMode.HALF_UP);
}

    @Override
@Transactional(readOnly = true)
public Page<PricingRuleResponseDto> findAll(Pageable pageable) {
    return pricingRuleRepository.findAll(pageable)
            .map(PricingMapper::toDto);
}

@Override
@Transactional(readOnly = true)
public Page<PricingRuleResponseDto> findByHotelId(Long hotelId, Pageable pageable) {
    if (hotelId == null) {
        throw new InvalidPricingRuleException("Hotel ID must not be null.");
    }

    Specification<PricingRule> spec = Specification
            .where(PricingSpecifications.hasHotel(hotelId));

    return pricingRuleRepository.findAll(spec, pageable)
            .map(PricingMapper::toDto);
}

@Override
@Transactional(readOnly = true)
public Page<PricingRuleResponseDto> findByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId, Pageable pageable) {
    if (hotelId == null) {
        throw new InvalidPricingRuleException("Hotel ID must not be null.");
    }
    if (roomTypeId == null) {
        throw new InvalidPricingRuleException("Room type ID must not be null.");
    }

    Specification<PricingRule> spec = Specification
            .where(PricingSpecifications.hasHotel(hotelId))
            .and(PricingSpecifications.hasRoomType(roomTypeId));

    return pricingRuleRepository.findAll(spec, pageable)
            .map(PricingMapper::toDto);
}
@Override
@Transactional(readOnly = true)
public PricingRule getPricingRuleEntityById(Long id) {
    return pricingRuleRepository.findById(id)
            .orElseThrow(() -> new PricingRuleNotFoundException("Pricing rule not found with id: " + id));
}
}