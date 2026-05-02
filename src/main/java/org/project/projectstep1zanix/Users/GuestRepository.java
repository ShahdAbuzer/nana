package org.project.projectstep1zanix.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByUserId(Long userId);

    Optional<Guest> findByUserUsername(String username);

    Optional<Guest> findByUserEmail(String email);
}