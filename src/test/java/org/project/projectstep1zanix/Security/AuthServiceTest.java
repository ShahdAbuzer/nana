package org.project.projectstep1zanix.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.project.projectstep1zanix.Users.*;
import org.project.projectstep1zanix.common.DuplicateResourceException;
import org.project.projectstep1zanix.common.ResourceNotFoundException;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AppUserRepository appUserRepository;
    @Mock private GuestRepository guestRepository;
    @Mock private ManagerRepository managerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private AppUser user;

    @BeforeEach
    void setup() {

        authService = new AuthService(
                appUserRepository,
                guestRepository,
                managerRepository,
                passwordEncoder,
                jwtTokenService,
                refreshTokenRepository,
                7
        );

        signupRequest = new SignupRequest(
                "testUser",
                "Password1!",
                "Password1!",
                "test@test.com",
                Role.GUEST
        );

        loginRequest = new LoginRequest("test@test.com", "Password1!");

        user = new AppUser();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRoles(Set.of(Role.GUEST));
    }

    // SIGNUP SUCCESS
    @Test
    void shouldSignupSuccessfully() {
        when(appUserRepository.existsByUsername(any())).thenReturn(false);
        when(appUserRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(appUserRepository.save(any())).thenReturn(user);

        SignupResponse response = authService.signup(signupRequest);

        assertNotNull(response);
        verify(appUserRepository).save(any());
        verify(guestRepository).save(any());
    }

    //  USERNAME EXISTS
    @Test
    void shouldThrowException_whenUsernameExists() {
        when(appUserRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> authService.signup(signupRequest));
    }

    // EMAIL EXISTS
    @Test
    void shouldThrowException_whenEmailExists() {
        when(appUserRepository.existsByUsername(any())).thenReturn(false);
        when(appUserRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> authService.signup(signupRequest));
    }

    //  PASSWORD MISMATCH
    @Test
    void shouldThrowException_whenPasswordsMismatch() {
        SignupRequest badRequest = new SignupRequest(
                "user", "pass1", "pass2", "email@test.com", Role.GUEST
        );

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup(badRequest));
    }

    //  LOGIN SUCCESS
    @Test
    void shouldLoginSuccessfully() {
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenService.generateAccessToken(any(), any(), any(), any()))
                .thenReturn("token");
        when(jwtTokenService.getAccessTokenExpiresInSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token", response.accessToken());
    }

    //  INVALID PASSWORD
    @Test
    void shouldThrowException_whenInvalidPassword() {
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(Exception.class,
                () -> authService.login(loginRequest));
    }

    //  USER NOT FOUND
    @Test
    void shouldThrowException_whenUserNotFound() {
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(appUserRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> authService.login(loginRequest));
    }

    //  REFRESH TOKEN SUCCESS
    @Test
    void shouldRefreshTokenSuccessfully() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(1000));
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));
        when(jwtTokenService.generateAccessToken(any(), any(), any(), any()))
                .thenReturn("newToken");
        when(jwtTokenService.getAccessTokenExpiresInSeconds()).thenReturn(3600L);

        LoginResponse response = authService.refreshToken(new RefreshTokenRequest("token"));

        assertNotNull(response);
        assertEquals("newToken", response.accessToken());
    }

    //  REFRESH TOKEN REVOKED
    @Test
    void shouldThrowException_whenTokenRevoked() {
        RefreshToken token = new RefreshToken();
        token.setRevoked(true);

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        assertThrows(Exception.class,
                () -> authService.refreshToken(new RefreshTokenRequest("token")));
    }

    //  LOGOUT TOKEN NOT FOUND
    @Test
    void shouldThrowException_whenLogoutTokenNotFound() {
        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.revokeRefreshToken("token"));
    }

    //  LOGOUT SUCCESS
    @Test
    void shouldRevokeTokenSuccessfully() {
        RefreshToken token = new RefreshToken();
        token.setRevoked(false);

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        authService.revokeRefreshToken("token");

        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }
}