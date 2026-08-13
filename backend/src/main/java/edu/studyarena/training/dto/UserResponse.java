package edu.studyarena.training.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {}