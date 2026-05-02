package org.project.projectstep1zanix.catalog.nearbyplace;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotels/{hotelId}/nearby-places")
public class NearbyPlaceController {

    private final NearbyPlaceService nearbyPlaceService;

    public NearbyPlaceController(NearbyPlaceService nearbyPlaceService) {
        this.nearbyPlaceService = nearbyPlaceService;
    }

    @Operation(summary = "Add nearby place", description = "Adds a nearby place to a hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nearby place created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<NearbyPlaceResponseDto> create(
            @PathVariable Long hotelId,
            @Valid @RequestBody NearbyPlaceRequestDto dto) {
        NearbyPlaceResponseDto created = nearbyPlaceService.createNearbyPlace(hotelId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "List nearby places", description = "Lists all nearby places for a hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nearby places retrieved")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<NearbyPlaceResponseDto>> getAll(@PathVariable Long hotelId) {
        return ResponseEntity.ok(nearbyPlaceService.getNearbyPlacesByHotelId(hotelId));
    }

    @Operation(summary = "Update nearby place", description = "Updates a nearby place.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nearby place updated"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/{placeId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<NearbyPlaceResponseDto> update(
            @PathVariable Long hotelId,
            @PathVariable Long placeId,
            @Valid @RequestBody NearbyPlaceRequestDto dto) {
        return ResponseEntity.ok(nearbyPlaceService.updateNearbyPlace(hotelId, placeId, dto));
    }

    @Operation(summary = "Delete nearby place", description = "Deletes a nearby place.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long hotelId, @PathVariable Long placeId) {
        nearbyPlaceService.deleteNearbyPlace(hotelId, placeId);
        return ResponseEntity.noContent().build();
    }
}
