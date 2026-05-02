package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface RoomTypeService {
    RoomTypeResponseDto createRoomType(RoomTypeRequestDto requestDto);

    RoomTypeResponseDto getRoomTypeById(Long id);

    PagedResponse<RoomTypeResponseDto> getAllRoomTypes(
            Long hotelId,
            String name,
            Integer capacity,
            Double minPrice,
            Double maxPrice,
            List<String> amenities,
            Pageable pageable
    );

    RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto requestDto);

    void deleteRoomType(Long id);

    RoomTypeResponseDto uploadImage(Long id, MultipartFile file);

    RoomTypeResponseDto replaceImage(Long id, MultipartFile file);

    void deleteImage(Long id);

    RoomType getRoomTypeEntityById(Long id);
}