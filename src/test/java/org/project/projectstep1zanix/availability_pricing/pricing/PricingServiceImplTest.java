package org.project.projectstep1zanix.availability_pricing.pricing;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.project.projectstep1zanix.availability_pricing.Pricing.InvalidPricingRuleException;
import org.project.projectstep1zanix.availability_pricing.Pricing.PriceQuoteRequestDto;
import org.project.projectstep1zanix.availability_pricing.Pricing.PriceQuoteResponseDto;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRuleNotFoundException;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRuleRepository;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingServiceImpl;
import org.project.projectstep1zanix.catalog.RoomType.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock private PricingRuleRepository pricingRuleRepository;
    @Mock private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private PriceQuoteRequestDto request;
    private RoomType roomType;

    @BeforeEach
    void setup() {
        request = new PriceQuoteRequestDto();
        request.setHotelId(1L);
        request.setRoomTypeId(1L);
        request.setGuests(2);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        roomType = new RoomType();
        roomType.setCapacity(3);
        roomType.setBasePrice(100.0);
    }

    // GET QUOTE SUCCESS
    @Test
    void shouldReturnPriceQuote() {
        when(roomTypeRepository.findByIdAndHotelId(1L, 1L))
                .thenReturn(Optional.of(roomType));

        when(pricingRuleRepository.findByHotelIdAndRoomTypeIdAndActiveTrue(1L, 1L))
                .thenReturn(List.of());

        PriceQuoteResponseDto result = pricingService.getQuote(request);

        assertNotNull(result);
        assertEquals(2, result.getNights());
    }

    //NVALID REQUEST
    @Test
    void shouldThrowException_whenInvalidRequest() {
        assertThrows(InvalidPricingRuleException.class,
                () -> pricingService.getQuote(null));
    }

    // INVALID DATE
    @Test
    void shouldThrowException_whenInvalidDates() {
        request.setEndDate(request.getStartDate());

        assertThrows(InvalidPricingRuleException.class,
                () -> pricingService.getQuote(request));
    }

    // CAPACITY EXCEEDED
    @Test
    void shouldThrowException_whenCapacityExceeded() {
        request.setGuests(10);

        when(roomTypeRepository.findByIdAndHotelId(1L, 1L))
                .thenReturn(Optional.of(roomType));

        assertThrows(InvalidPricingRuleException.class,
                () -> pricingService.getQuote(request));
    }

    // ROOM TYPE NOT FOUND
    @Test
    void shouldThrowException_whenRoomTypeNotFound() {
        when(roomTypeRepository.findByIdAndHotelId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(PricingRuleNotFoundException.class,
                () -> pricingService.getQuote(request));
    }
}