package org.project.projectstep1zanix.roomtype;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeMapper;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeNotFoundException;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRequestDto;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeResponseDto;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeServiceImpl;
import org.project.projectstep1zanix.file.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceImplTest {

    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private RoomTypeMapper roomTypeMapper;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    private RoomTypeRequestDto request;
    private RoomType roomType;
    private Hotel hotel;

    @BeforeEach
    void setup() {
        request = new RoomTypeRequestDto();
        request.setHotelId(1L);

        hotel = new Hotel();
        hotel.setId(1L);

        roomType = new RoomType();
        roomType.setId(1L);
    }

    //  CREATE SUCCESS
    @Test
    void shouldCreateRoomTypeSuccessfully() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomTypeMapper.toEntity(request)).thenReturn(roomType);
        when(roomTypeRepository.save(any())).thenReturn(roomType);
        when(roomTypeMapper.toDto(any())).thenReturn(new RoomTypeResponseDto());

        RoomTypeResponseDto result = roomTypeService.createRoomType(request);

        assertNotNull(result);
        verify(roomTypeRepository).save(any());
    }

    //  HOTEL NOT FOUND
    @Test
    void shouldThrowException_whenHotelNotFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,
                () -> roomTypeService.createRoomType(request));
    }

    //  GET BY ID
    @Test
    void shouldReturnRoomTypeById() {
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(roomTypeMapper.toDto(roomType)).thenReturn(new RoomTypeResponseDto());

        RoomTypeResponseDto result = roomTypeService.getRoomTypeById(1L);

        assertNotNull(result);
    }

    //  NOT FOUND
    @Test
    void shouldThrowException_whenRoomTypeNotFound() {
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomTypeNotFoundException.class,
                () -> roomTypeService.getRoomTypeById(1L));
    }

    //  UPDATE SUCCESS
    @Test
    void shouldUpdateRoomTypeSuccessfully() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(roomTypeRepository.save(any())).thenReturn(roomType);
        when(roomTypeMapper.toDto(any())).thenReturn(new RoomTypeResponseDto());

        RoomTypeResponseDto result =
                roomTypeService.updateRoomType(1L, request);

        assertNotNull(result);
        verify(roomTypeMapper).updateEntityFromDto(any(), any());
    }

    //  UPDATE NOT FOUND
    @Test
    void shouldThrowException_whenUpdatingNonExistingRoomType() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomTypeNotFoundException.class,
                () -> roomTypeService.updateRoomType(1L, request));
    }

    //  DELETE SUCCESS
    @Test
    void shouldDeleteRoomType() {
        when(roomTypeRepository.existsById(1L)).thenReturn(true);

        roomTypeService.deleteRoomType(1L);

        verify(roomTypeRepository).deleteById(1L);
    }

    //  DELETE NOT FOUND
    @Test
    void shouldThrowException_whenDeletingNonExistingRoomType() {
        when(roomTypeRepository.existsById(1L)).thenReturn(false);

        assertThrows(RoomTypeNotFoundException.class,
                () -> roomTypeService.deleteRoomType(1L));
    }

    //  UPLOAD IMAGE
    @Test
    void shouldUploadImageSuccessfully() {
        MultipartFile file = mock(MultipartFile.class);

        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(fileStorageService.saveFile(file, "roomtypes"))
                .thenReturn("image.jpg");
        when(roomTypeRepository.save(any())).thenReturn(roomType);
        when(roomTypeMapper.toDto(any())).thenReturn(new RoomTypeResponseDto());

        RoomTypeResponseDto result =
                roomTypeService.uploadImage(1L, file);

        assertNotNull(result);
        verify(fileStorageService).saveFile(file, "roomtypes");
    }

    //  REPLACE IMAGE
    @Test
    void shouldReplaceImageSuccessfully() {
        MultipartFile file = mock(MultipartFile.class);
        roomType.setImageUrl("old.jpg");

        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(fileStorageService.saveFile(file, "roomtypes"))
                .thenReturn("new.jpg");

        roomTypeService.replaceImage(1L, file);

        verify(fileStorageService).deleteFile("old.jpg");
        verify(fileStorageService).saveFile(file, "roomtypes");
    }

    //  DELETE IMAGE
    @Test
    void shouldDeleteImageSuccessfully() {
        roomType.setImageUrl("img.jpg");

        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));

        roomTypeService.deleteImage(1L);

        verify(fileStorageService).deleteFile("img.jpg");
    }
}
