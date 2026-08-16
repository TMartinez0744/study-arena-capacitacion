package edu.studyarena.training.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}