package org.project.projectstep1zanix.catalog.Hotel;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import org.project.projectstep1zanix.catalog.nearbyplace.NearbyPlaceResponseDto;
import org.project.projectstep1zanix.catalog.nearbyplace.NearbyPlaceService;
import org.project.projectstep1zanix.catalog.review.ReviewResponseDto;
import org.project.projectstep1zanix.catalog.review.ReviewService;

import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HotelMapper hotelMapper;
    private final HotelImageService hotelImageService;
    private final NearbyPlaceService nearbyPlaceService;
    private final ReviewService reviewService;

    public HotelController(HotelService hotelService, HotelMapper hotelMapper,
                           HotelImageService hotelImageService,
                           NearbyPlaceService nearbyPlaceService,
                           ReviewService reviewService) {
        this.hotelService = hotelService;
        this.hotelMapper = hotelMapper;
        this.hotelImageService = hotelImageService;
        this.nearbyPlaceService = nearbyPlaceService;
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create hotel", description = "Creates a new hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponseDto> createHotel(
            @Valid @RequestBody HotelRequestDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        Hotel hotel = hotelMapper.toEntity(dto);
        Hotel savedHotel = hotelService.createHotel(hotel);

        URI location = uriBuilder.path("/api/hotels/{id}")
                .buildAndExpand(savedHotel.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(hotelMapper.toResponseDto(savedHotel));
    }

    @Operation(summary = "Get hotel by ID", description = "Retrieves full hotel details by its ID, including nearby places and reviews.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public HotelResponseDto getHotelById(@PathVariable Long id) {
        Hotel hotel = hotelService.getHotelById(id);
        HotelResponseDto dto = hotelMapper.toResponseDto(hotel);

        // Enrich with nearby places and reviews
        List<NearbyPlaceResponseDto> nearbyPlaces = nearbyPlaceService.getNearbyPlacesByHotelId(id);
        List<ReviewResponseDto> reviews = reviewService.getReviewsByHotelId(id);
        dto.setNearbyPlaces(nearbyPlaces);
        dto.setReviews(reviews);

        return dto;
    }

    @Operation(summary = "List hotels", description = "Returns a paginated list of hotels with optional filters: country, city, minRating, maxPrice, amenities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public HotelPagedResponse<HotelResponseDto> getAllHotels(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> amenities,
            Pageable pageable
    ) {
        HotelPagedResponse<Hotel> hotels = hotelService.findAll(country, city, minRating, maxPrice, amenities, pageable);

        return new HotelPagedResponse<>(
                hotels.getContent().stream()
                        .map(hotelMapper::toResponseDto)
                        .toList(),
                hotels.getPage(),
                hotels.getSize(),
                hotels.getTotalElements(),
                hotels.getTotalPages()
        );
    }

    @Operation(summary = "Update hotel", description = "Updates hotel details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isManagerOfHotel(authentication, #id)")
    public HotelResponseDto updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequestDto dto
    ) {
        Hotel hotel = hotelMapper.toEntity(dto);
        Hotel updatedHotel = hotelService.updateHotel(id, hotel);
        return hotelMapper.toResponseDto(updatedHotel);
    }

    @Operation(summary = "Delete hotel", description = "Deletes a hotel by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isManagerOfHotel(authentication, #id)")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload hotel image", description = "Uploads an image for the hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or file limits exceeded"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or @authz.isManagerOfHotel(authentication, #id)")
    public ResponseEntity<HotelResponseDto> uploadHotelImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        HotelResponseDto updatedHotel = hotelImageService.uploadImage(id, image);
        return ResponseEntity.ok(updatedHotel);
    }

    @Operation(summary = "Update hotel image", description = "Updates the existing hotel image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping(value = "/{id}/image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or @authz.isManagerOfHotel(authentication, #id)")
    public ResponseEntity<HotelResponseDto> updateHotelImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        HotelResponseDto updatedHotel = hotelImageService.updateImage(id, image);
        return ResponseEntity.ok(updatedHotel);
    }

    @Operation(summary = "Delete hotel image", description = "Deletes the hotel's image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN') or @authz.isManagerOfHotel(authentication, #id)")
    public ResponseEntity<Void> deleteHotelImage(@PathVariable Long id) {
        hotelImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get hotel image URL", description = "Returns the full URL of the hotel image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Image not found for this hotel")
    })
    @GetMapping("/{id}/image")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> getHotelImageUrl(
            @PathVariable Long id,
            HttpServletRequest request) {
        String imagePath = hotelImageService.getImageUrl(id);

        // Build full absolute URL: e.g. http://localhost:8080/uploads/hotels/uuid.jpg
        String fullUrl = request.getScheme() + "://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + imagePath;

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", fullUrl);
        return ResponseEntity.ok(response);
    }
}