package org.project.projectstep1zanix.availability_pricing.Availability;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }
@Operation(summary = "Get availability by ID", description = "Retrieves availability details by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Availability not found")
})
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GUEST') or @authz.canManageAvailability(authentication, #id)")
    public ResponseEntity<AvailabilityDetailsDto> getAvailability(@PathVariable Long id) {
        Availability entity = availabilityService.findById(id);
        return ResponseEntity.ok(AvailabilityMapper.toDetailsDto(entity));
    }
   @Operation(summary = "Search availability", description = "Searches availability by hotel, room type, and date range.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability list retrieved successfully")
})
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','GUEST')")
    public ResponseEntity<PagedResponse<AvailabilityDetailsDto>> listAvailabilities(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        Page<Availability> page = availabilityService.searchAvailabilities(
                hotelId, roomTypeId, startDate, endDate, pageable
        );

        List<AvailabilityDetailsDto> content = page.getContent()
                .stream()
                .map(AvailabilityMapper::toDetailsDto)
                .toList();

        return ResponseEntity.ok(PagedResponse.from(page, content));
    }
@Operation(summary = "Reserve availability", description = "Creates a reservation entry for room availability.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Availability reserved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid availability data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Room is not available")
})
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authz.canCreateAvailability(authentication, #request)")
    public ResponseEntity<AvailabilityDetailsDto> reserveAvailability(
            @Valid @RequestBody AvailabilityRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        Availability entity = availabilityService.reserve(AvailabilityMapper.toEntity(request));
        AvailabilityDetailsDto responseDto = AvailabilityMapper.toDetailsDto(entity);

        URI location = uriBuilder.path("/availability/{id}")
                .buildAndExpand(entity.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }
@Operation(summary = "Update availability", description = "Updates an existing availability record.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid availability data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Availability not found")
})
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageAvailability(authentication, #id)")
    public ResponseEntity<AvailabilityDetailsDto> replaceAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequestDto request
    ) {
        Availability updatedEntity = AvailabilityMapper.toEntity(request);
        Availability entity = availabilityService.replace(id, updatedEntity);
        return ResponseEntity.ok(AvailabilityMapper.toDetailsDto(entity));
    }
@Operation(summary = "Delete availability", description = "Deletes an availability record by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Availability deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Availability not found")
})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageAvailability(authentication, #id)")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
@Operation(summary = "Delete availability", description = "Deletes an availability record by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Availability deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Availability not found")
})
    @PostMapping("/check")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AvailabilityResponseDto> checkAvailability(
            @Valid @RequestBody AvailabilityRequestDto request
    ) {
        AvailabilityResponseDto response = availabilityService.checkAvailability(request, null);
        return ResponseEntity.ok(response);
    }
}