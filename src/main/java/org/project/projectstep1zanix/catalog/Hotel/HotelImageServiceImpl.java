package org.project.projectstep1zanix.catalog.Hotel;

import org.project.projectstep1zanix.file.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class HotelImageServiceImpl implements HotelImageService {

    private final HotelRepository hotelRepository;
    private final FileStorageService fileStorageService;
    private final HotelMapper hotelMapper;

    public HotelImageServiceImpl(
            HotelRepository hotelRepository,
            FileStorageService fileStorageService,
            HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.fileStorageService = fileStorageService;
        this.hotelMapper = hotelMapper;
    }

    // ─── Upload ──────────────────────────────────────────────────────────────

    @Override
    public HotelResponseDto uploadImage(Long hotelId, MultipartFile file) {
        Hotel hotel = findHotelOrThrow(hotelId);

        if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty()) {
            throw new IllegalStateException(
                    "Hotel already has an image. Use the PUT endpoint to replace it.");
        }

        String imageUrl = fileStorageService.saveFile(file, "hotels");
        hotel.setImageUrl(imageUrl);
        hotelRepository.save(hotel);

        return hotelMapper.toResponseDto(hotel);
    }

    // ─── Update ──────────────────────────────────────────────────────────────

    @Override
    public HotelResponseDto updateImage(Long hotelId, MultipartFile file) {
        Hotel hotel = findHotelOrThrow(hotelId);

        // Delete the old image file if one exists
        String oldImageUrl = hotel.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            fileStorageService.deleteFile(oldImageUrl);
        }

        String newImageUrl = fileStorageService.saveFile(file, "hotels");
        hotel.setImageUrl(newImageUrl);
        hotelRepository.save(hotel);

        return hotelMapper.toResponseDto(hotel);
    }

    // ─── Delete ──────────────────────────────────────────────────────────────

    @Override
    public void deleteImage(Long hotelId) {
        Hotel hotel = findHotelOrThrow(hotelId);

        String imageUrl = hotel.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalStateException("Hotel with ID " + hotelId + " has no image to delete.");
        }

        fileStorageService.deleteFile(imageUrl);
        hotel.setImageUrl(null);
        hotelRepository.save(hotel);
    }

    // ─── Get URL ─────────────────────────────────────────────────────────────

    @Override
    public String getImageUrl(Long hotelId) {
        Hotel hotel = findHotelOrThrow(hotelId);

        if (hotel.getImageUrl() == null || hotel.getImageUrl().isEmpty()) {
            throw new HotelNotFoundException("No image found for hotel with ID: " + hotelId);
        }

        return hotel.getImageUrl();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Hotel findHotelOrThrow(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));
    }
}
