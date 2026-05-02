package org.project.projectstep1zanix.Security;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.Users.LoginRequest;
import org.project.projectstep1zanix.Users.LoginResponse;
import org.project.projectstep1zanix.Users.Manager;
import org.project.projectstep1zanix.Users.ManagerRepository;
import org.project.projectstep1zanix.Users.Role;
import org.project.projectstep1zanix.Users.SignupRequest;
import org.project.projectstep1zanix.Users.SignupResponse;
import org.project.projectstep1zanix.common.DuplicateResourceException;
import org.project.projectstep1zanix.common.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final GuestRepository guestRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenDays;

    public AuthService(
            AppUserRepository appUserRepository,
            GuestRepository guestRepository,
            ManagerRepository managerRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-token-days}") long refreshTokenDays
    ) {
        this.appUserRepository = appUserRepository;
        this.guestRepository = guestRepository;
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (appUserRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username is already taken");
        }

        if (appUserRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already in use");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setEmail(request.email());
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setEnabled(true);
        appUser.setRoles(Set.of(request.role()));

        AppUser savedUser = appUserRepository.save(appUser);

        if (request.role() == Role.GUEST) {
            Guest guest = new Guest();
            guest.setFullName(savedUser.getUsername());
            guest.setUser(savedUser);
            guestRepository.save(guest);

        } else if (request.role() == Role.MANAGER) {
            Manager manager = new Manager();
            manager.setFullName(savedUser.getUsername());
            manager.setUser(savedUser);
            managerRepository.save(manager);
        }

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                request.role(),
                "User registered successfully"
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        AppUser user = appUserRepository.findByEmail(request.login())
                .or(() -> appUserRepository.findByUsername(request.login()))
                .filter(AppUser::isEnabled)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::name)
                .toList();

        String accessToken = jwtTokenService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );

        String refreshToken = createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenService.getAccessTokenExpiresInSeconds()
        );
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        AppUser user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new InvalidRefreshTokenException("User account is disabled");
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::name)
                .toList();

        String accessToken = jwtTokenService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );

        String newRefreshToken = rotateRefreshToken(refreshToken);

        return new LoginResponse(
                accessToken,
                newRefreshToken,
                "Bearer",
                jwtTokenService.getAccessTokenExpiresInSeconds()
        );
    }

    @Transactional
    public void revokeRefreshToken(String tokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private String createRefreshToken(AppUser user) {
        String tokenValue = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenDays * 24 * 60 * 60);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    private String rotateRefreshToken(RefreshToken oldRefreshToken) {
        refreshTokenRepository.delete(oldRefreshToken);
        return createRefreshToken(oldRefreshToken.getUser());
    }
}