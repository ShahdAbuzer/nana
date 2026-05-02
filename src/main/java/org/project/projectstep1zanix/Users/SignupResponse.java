package org.project.projectstep1zanix.Users;

public record SignupResponse(
    Long userId,
    String username,
    String email,
    Role role,
    String message
) {}