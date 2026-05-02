package org.project.projectstep1zanix.catalog.Hotel;

import java.util.List;

import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Manager;
import org.project.projectstep1zanix.Users.ManagerRepository;
import org.project.projectstep1zanix.Users.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final AppUserRepository appUserRepository;
    private final ManagerRepository managerRepository;

    public HotelServiceImpl(
            HotelRepository hotelRepository,
            AppUserRepository appUserRepository,
            ManagerRepository managerRepository
    ) {
        this.hotelRepository = hotelRepository;
        this.appUserRepository = appUserRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();

        AppUser user = appUserRepository.findByEmail(login)
                .or(() -> appUserRepository.findByUsername(login))
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (user.getRoles().contains(Role.MANAGER)) {
            Manager manager = managerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Manager not found for authenticated user"));
            hotel.setManager(manager);
        } else if (user.getRoles().contains(Role.ADMIN)) {
            hotel.setManager(null);
        } else {
            throw new RuntimeException("Unauthorized role");
        }

        return hotelRepository.save(hotel);
    }

    @Override
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + id));
    }

    @Override
    public HotelPagedResponse<Hotel> findAll(String country, String city, Double minRating,
                                              Double maxPrice, List<String> amenities,
                                              Pageable pageable) {
        Specification<Hotel> spec = Specification.where(HotelSpecifications.hasCountry(country))
                .and(HotelSpecifications.hasCity(city))
                .and(HotelSpecifications.hasMinRating(minRating))
                .and(HotelSpecifications.hasMaxRoomPrice(maxPrice))
                .and(HotelSpecifications.hasAmenities(amenities));

        Page<Hotel> hotelsPage = hotelRepository.findAll(spec, pageable);

        return convertPageToPagedResponse(hotelsPage);
    }

    @Override
    public Hotel updateHotel(Long id, Hotel hotel) {
        Hotel existingHotel = getHotelById(id);
        existingHotel.setName(hotel.getName());
        existingHotel.setCity(hotel.getCity());
        existingHotel.setCountry(hotel.getCountry());
        existingHotel.setDescription(hotel.getDescription());
        existingHotel.setRating(hotel.getRating());
        existingHotel.setAddress(hotel.getAddress());
        existingHotel.setLatitude(hotel.getLatitude());
        existingHotel.setLongitude(hotel.getLongitude());
        existingHotel.setAmenities(hotel.getAmenities());
        return hotelRepository.save(existingHotel);
    }

    @Override
    public void deleteHotel(Long id) {
        Hotel hotel = getHotelById(id);
        hotelRepository.delete(hotel);
    }

    private HotelPagedResponse<Hotel> convertPageToPagedResponse(Page<Hotel> hotelsPage) {
        HotelPagedResponse<Hotel> pagedResponse = new HotelPagedResponse<>();
        pagedResponse.setContent(hotelsPage.getContent());
        pagedResponse.setPage(hotelsPage.getNumber());
        pagedResponse.setSize(hotelsPage.getSize());
        pagedResponse.setTotalElements(hotelsPage.getTotalElements());
        pagedResponse.setTotalPages(hotelsPage.getTotalPages());
        return pagedResponse;
    }
}
