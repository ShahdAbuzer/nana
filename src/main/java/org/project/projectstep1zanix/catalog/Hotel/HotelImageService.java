package org.project.projectstep1zanix.catalog.Hotel;

import org.springframework.web.multipart.MultipartFile;

public interface HotelImageService {
    HotelResponseDto uploadImage(Long hotelId, MultipartFile file);
    HotelResponseDto updateImage(Long hotelId, MultipartFile file);
    void deleteImage(Long hotelId);
    String getImageUrl(Long hotelId);
}
