package org.project.projectstep1zanix.Users;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds
) {}