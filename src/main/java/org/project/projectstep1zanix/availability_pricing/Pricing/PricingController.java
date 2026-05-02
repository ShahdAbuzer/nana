package org.project.projectstep1zanix.availability_pricing.Pricing;

import java.net.URI;
import java.time.LocalDate;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }
@Operation(summary = "Get price quote", description = "Calculates the booking price based on pricing rules.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Price quote calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pricing request")
})
    @PostMapping("/quote")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PriceQuoteResponseDto> getQuote(
            @Valid @RequestBody PriceQuoteRequestDto request
    ) {
        return ResponseEntity.ok(pricingService.getQuote(request));
    }
@Operation(summary = "List pricing rules", description = "Returns pricing rules with optional filters.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pricing rules retrieved successfully")
})
    @GetMapping("/rules")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','GUEST')")
    public ResponseEntity<PagedResponse<PricingRuleResponseDto>> listRules(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) PricingRuleType ruleType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                pricingService.searchRules(
                        hotelId,
                        roomTypeId,
                        ruleType,
                        active,
                        startDate,
                        endDate,
                        pageable
                )
        );
    }
@Operation(summary = "Create pricing rule", description = "Creates a new pricing rule.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pricing rule created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pricing rule data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
})
    @GetMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUEST') or @authz.canManagePricingRule(authentication, #id)")
    public ResponseEntity<PricingRuleResponseDto> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(pricingService.findById(id));
    }
    @Operation(summary = "Create pricing rule", description = "Creates a new pricing rule.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pricing rule created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pricing rule data"),
        @ApiResponse(responseCode = "403", description = "Access denied")  })

    @PostMapping("/rules")
    @PreAuthorize("hasRole('ADMIN') or @authz.canCreatePricingRule(authentication, #request)")
    public ResponseEntity<PricingRuleResponseDto> createRule(
            @Valid @RequestBody PricingRuleRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        PricingRuleResponseDto created = pricingService.create(request);

        URI location = uriBuilder.path("/pricing/rules/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }
    @Operation(summary = "Update pricing rule", description = "Updates an existing pricing rule.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pricing rule updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pricing rule data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Pricing rule not found")
})

    @PutMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManagePricingRule(authentication, #id)")
    public ResponseEntity<PricingRuleResponseDto> replaceRule(
            @PathVariable Long id,
            @Valid @RequestBody PricingRuleRequestDto request
    ) {
        return ResponseEntity.ok(pricingService.replace(id, request));
    }
@Operation(summary = "Delete pricing rule", description = "Deletes a pricing rule by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pricing rule deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Pricing rule not found")
})
    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManagePricingRule(authentication, #id)")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        pricingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}