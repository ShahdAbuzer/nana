package org.project.projectstep1zanix.catalog.RoomType;

import java.util.List;

import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.file.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeMapper roomTypeMapper;
    private final FileStorageService fileStorageService;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository,
                               HotelRepository hotelRepository,
                               RoomTypeMapper roomTypeMapper,
                               FileStorageService fileStorageService) {
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeMapper = roomTypeMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public RoomTypeResponseDto createRoomType(RoomTypeRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(requestDto.getHotelId()));
        RoomType entity = roomTypeMapper.toEntity(requestDto);
        entity.setHotel(hotel);
        RoomType saved = roomTypeRepository.save(entity);
        return roomTypeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponseDto getRoomTypeById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));
        return roomTypeMapper.toDto(roomType);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomTypeResponseDto> getAllRoomTypes(Long hotelId, String name,
            Integer capacity, Double minPrice, Double maxPrice,
            List<String> amenities, Pageable pageable) {
        Specification<RoomType> spec = Specification.where(RoomTypeSpecification.hasHotelId(hotelId))
                .and(RoomTypeSpecification.hasName(name))
                .and(RoomTypeSpecification.hasCapacity(capacity))
                .and(RoomTypeSpecification.hasMinPrice(minPrice))
                .and(RoomTypeSpecification.hasMaxPrice(maxPrice))
                .and(RoomTypeSpecification.hasAmenities(amenities));
        Page<RoomType> page = roomTypeRepository.findAll(spec, pageable);
        Page<RoomTypeResponseDto> dtoPage = page.map(roomTypeMapper::toDto);
        return PagedResponse.from(page, dtoPage.getContent());
    }

    @Override
    @Transactional
    public RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(requestDto.getHotelId()));
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));
        roomTypeMapper.updateEntityFromDto(requestDto, roomType);
        roomType.setHotel(hotel);
        RoomType updated = roomTypeRepository.save(roomType);
        return roomTypeMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RoomTypeNotFoundException(id);
        }
        roomTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoomTypeResponseDto uploadImage(Long id, MultipartFile file) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
        rt.setImageUrl(fileStorageService.saveFile(file, "roomtypes"));
        return roomTypeMapper.toDto(roomTypeRepository.save(rt));
    }

    @Override
    @Transactional
    public RoomTypeResponseDto replaceImage(Long id, MultipartFile file) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
        if (rt.getImageUrl() != null) fileStorageService.deleteFile(rt.getImageUrl());
        rt.setImageUrl(fileStorageService.saveFile(file, "roomtypes"));
        return roomTypeMapper.toDto(roomTypeRepository.save(rt));
    }

    @Override
    @Transactional
    public void deleteImage(Long id) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
        if (rt.getImageUrl() != null) {
            fileStorageService.deleteFile(rt.getImageUrl());
            rt.setImageUrl(null);
            roomTypeRepository.save(rt);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoomType getRoomTypeEntityById(Long id) {
        return roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
    }
}