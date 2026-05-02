package org.project.projectstep1zanix.catalog.RoomType;

import java.net.URI;
import java.util.List;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @Operation(summary = "Create room type", description = "Creates a new room type for a hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid room type data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authz.canCreateRoomType(authentication, #requestDto)")
    public ResponseEntity<RoomTypeResponseDto> createRoomType(
            @Valid @RequestBody RoomTypeRequestDto requestDto,
            UriComponentsBuilder uriBuilder) {
        RoomTypeResponseDto created = roomTypeService.createRoomType(requestDto);
        URI location = uriBuilder.path("/api/room-types/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get room type by ID", description = "Retrieves room type details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RoomTypeResponseDto> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    }

    @Operation(summary = "List room types", description = "Returns paginated room types with optional filters: hotelId, name, capacity, minPrice, maxPrice, amenities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room types retrieved successfully")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PagedResponse<RoomTypeResponseDto>> getAllRoomTypes(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> amenities,
            Pageable pageable) {
        return ResponseEntity.ok(
                roomTypeService.getAllRoomTypes(hotelId, name, capacity, minPrice, maxPrice, amenities, pageable));
    }

    @Operation(summary = "Update room type", description = "Updates room type details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid room type data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Room type or hotel not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageRoomType(authentication, #id)")
    public ResponseEntity<RoomTypeResponseDto> updateRoomType(
            @PathVariable Long id, @Valid @RequestBody RoomTypeRequestDto requestDto) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, requestDto));
    }

    @Operation(summary = "Delete room type", description = "Deletes a room type by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room type deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageRoomType(authentication, #id)")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload room type image", description = "Uploads an image for the room type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageRoomType(authentication, #id)")
    public ResponseEntity<RoomTypeResponseDto> uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(roomTypeService.uploadImage(id, file));
    }

    @Operation(summary = "Replace room type image", description = "Replaces the current image for the specified room type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image replaced successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    @PutMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageRoomType(authentication, #id)")
    public ResponseEntity<RoomTypeResponseDto> replaceImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(roomTypeService.replaceImage(id, file));
    }

    @Operation(summary = "Delete room type image", description = "Deletes the room type's image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageRoomType(authentication, #id)")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        roomTypeService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}