package org.project.projectstep1zanix.availability_pricing.Availability;

import java.time.LocalDate;
import java.util.List;

import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final RoomTypeRepository roomTypeRepository;

    public AvailabilityServiceImpl(
            AvailabilityRepository availabilityRepository,
            RoomTypeRepository roomTypeRepository
    ) {
        this.availabilityRepository = availabilityRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Availability> findAllPageable(Pageable pageable) {
        return availabilityRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Availability> searchAvailabilities(
            Long hotelId,
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Availability> spec = Specification
                .where(AvailabilitySpecifications.hasHotel(hotelId))
                .and(AvailabilitySpecifications.hasRoomType(roomTypeId))
                .and(AvailabilitySpecifications.overlaps(startDate, endDate));

        return availabilityRepository.findAll(spec, pageable);
    }
    private AvailabilityResponseDto checkInventoryAvailability(
        Long hotelId,
        Long roomTypeId,
        LocalDate startDate,
        LocalDate endDate,
        int roomsRequested,
        Long excludeAvailabilityId
) {
    int totalRooms = getTotalRooms(hotelId, roomTypeId);

    int reservedRooms = availabilityRepository.sumReservedRooms(
            hotelId,
            roomTypeId,
            startDate,
            endDate,
            excludeAvailabilityId,
            AvailabilityStatus.CANCELED
    );

    int availableRooms = Math.max(totalRooms - reservedRooms, 0);

    return new AvailabilityResponseDto(
            availableRooms >= roomsRequested,
            availableRooms,
            startDate,
            endDate
    );
}

   @Override
public Availability reserve(Availability availability) {
    validateAvailabilityInput(availability);
    checkDate(availability.getStartDate(), availability.getEndDate());

    boolean alreadyExists = availabilityRepository
            .existsByHotelIdAndRoomTypeIdAndBookingIdAndStartDateAndEndDate(
                    availability.getHotelId(),
                    availability.getRoomTypeId(),
                    availability.getBookingId(),
                    availability.getStartDate(),
                    availability.getEndDate()
            );

    if (alreadyExists) {
        throw new AvailabilityConflictException("Availability already exists.");
    }

    AvailabilityResponseDto response = checkInventoryAvailability(
            availability.getHotelId(),
            availability.getRoomTypeId(),
            availability.getStartDate(),
            availability.getEndDate(),
            availability.getRoomsReserved(),
            null
    );

    if (!response.isAvailable()) {
        throw new AvailabilityConflictException(
                "Not enough rooms available. Remaining: " + response.getRemainingRooms()
        );
    }

    if (availability.getStatus() == null) {
        availability.setStatus(AvailabilityStatus.PENDING);
    }

    return availabilityRepository.save(availability);
}

    @Override
    @Transactional(readOnly = true)
    public Availability findById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() ->
                        new AvailabilityNotFoundException("Availability not found with id: " + id));
    }

@Override
public Availability replace(Long id, Availability newAvailability) {
    validateAvailabilityInput(newAvailability);
    checkDate(newAvailability.getStartDate(), newAvailability.getEndDate());

    Availability existing = availabilityRepository.findById(id)
            .orElseThrow(() ->
                    new AvailabilityNotFoundException("Availability not found with id: " + id));

    AvailabilityResponseDto response = checkInventoryAvailability(
            newAvailability.getHotelId(),
            newAvailability.getRoomTypeId(),
            newAvailability.getStartDate(),
            newAvailability.getEndDate(),
            newAvailability.getRoomsReserved(),
            existing.getId()
    );

    if (!response.isAvailable()) {
        throw new AvailabilityConflictException(
                "Cannot update availability for hotel ID " + newAvailability.getHotelId() +
                ", room type ID " + newAvailability.getRoomTypeId() +
                " from " + newAvailability.getStartDate() +
                " to " + newAvailability.getEndDate() +
                ". Rooms remaining: " + response.getRemainingRooms()
        );
    }

    existing.setHotelId(newAvailability.getHotelId());
    existing.setRoomTypeId(newAvailability.getRoomTypeId());
    existing.setBookingId(newAvailability.getBookingId());
    existing.setStartDate(newAvailability.getStartDate());
    existing.setEndDate(newAvailability.getEndDate());
    existing.setRoomsReserved(newAvailability.getRoomsReserved());

    if (newAvailability.getStatus() != null) {
        existing.setStatus(newAvailability.getStatus());
    }

    return availabilityRepository.save(existing);
}

    @Override
    public void deleteById(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new AvailabilityNotFoundException("Availability not found with id: " + id);
        }
        availabilityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponseDto checkAvailability(AvailabilityRequestDto dto, Long excludeAvailabilityId) {
        validateRequestDto(dto);
        checkDate(dto.getStartDate(), dto.getEndDate());

        int totalRooms = getTotalRooms(dto.getHotelId(), dto.getRoomTypeId());
        int roomCapacity = getRoomCapacity(dto.getHotelId(), dto.getRoomTypeId());

        if (dto.getGuests() > roomCapacity) {
            return new AvailabilityResponseDto(
                    false,
                    0,
                    dto.getStartDate(),
                    dto.getEndDate()
            );
        }

        int reservedRooms = availabilityRepository.sumReservedRooms(
                dto.getHotelId(),
                dto.getRoomTypeId(),
                dto.getStartDate(),
                dto.getEndDate(),
                excludeAvailabilityId,
                AvailabilityStatus.CANCELED
        );

        int availableRooms = Math.max(totalRooms - reservedRooms, 0);

        return new AvailabilityResponseDto(
                availableRooms >= dto.getRoomsRequested(),
                availableRooms,
                dto.getStartDate(),
                dto.getEndDate()
        );
    }

    public void checkDate(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new InvalidDateRangeException("Start and end date must not be null.");
        }

        if (!end.isAfter(start)) {
            throw new InvalidDateRangeException("End date must be after start date.");
        }
    }

    public int getTotalRooms(Long hotelId, Long roomTypeId) {
        RoomType roomType = getRoomType(hotelId, roomTypeId);

        if (roomType.getTotalRooms() == null || roomType.getTotalRooms() <= 0) {
            throw new AvailabilityNotFoundException("Total rooms not configured for room type ID: " + roomTypeId);
        }

        return roomType.getTotalRooms();
    }

    public int getRoomCapacity(Long hotelId, Long roomTypeId) {
        RoomType roomType = getRoomType(hotelId, roomTypeId);

        if (roomType.getCapacity() == null || roomType.getCapacity() <= 0) {
            throw new AvailabilityNotFoundException("Capacity not configured for room type ID: " + roomTypeId);
        }

        return roomType.getCapacity();
    }

    private RoomType getRoomType(Long hotelId, Long roomTypeId) {
        return roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
                .orElseThrow(() -> new AvailabilityNotFoundException(
                        "Room type " + roomTypeId + " not found for hotel " + hotelId
                ));
    }


    private void validateAvailabilityInput(Availability availability) {
        if (availability == null) {
            throw new InvalidAvailabilityRequestException("Availability must not be null.");
        }
        if (availability.getHotelId() == null) {
            throw new InvalidAvailabilityRequestException("Hotel ID must not be null.");
        }
        if (availability.getRoomTypeId() == null) {
            throw new InvalidAvailabilityRequestException("Room type ID must not be null.");
        }
        if (availability.getRoomsReserved() <= 0) {
            throw new InvalidAvailabilityRequestException("Rooms reserved must be greater than 0.");
        }
    }

    private void validateRequestDto(AvailabilityRequestDto dto) {
        if (dto == null) {
            throw new InvalidAvailabilityRequestException("Availability request must not be null.");
        }
        if (dto.getHotelId() == null) {
            throw new InvalidAvailabilityRequestException("Hotel ID must not be null.");
        }
        if (dto.getRoomTypeId() == null) {
            throw new InvalidAvailabilityRequestException("Room type ID must not be null.");
        }
        if (dto.getRoomsRequested() <= 0) {
            throw new InvalidAvailabilityRequestException("Rooms requested must be greater than 0.");
        }
        if (dto.getGuests() == null || dto.getGuests() <= 0) {
            throw new InvalidAvailabilityRequestException("Guests must be greater than 0.");
        }
    }
}