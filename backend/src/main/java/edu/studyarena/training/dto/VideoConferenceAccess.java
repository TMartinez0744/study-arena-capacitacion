package edu.studyarena.training.dto;

import java.time.Instant;

// acceso a una sala
public record VideoConferenceAccess(
        String domain,  //dde entrar
        String roomName,    //q sala
        String token,
        Instant expiresAt
) {}
