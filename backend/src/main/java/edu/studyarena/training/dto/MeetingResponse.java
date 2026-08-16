package edu.studyarena.training.dto;

import edu.studyarena.training.entity.Meeting;

import java.time.Instant;

public record MeetingResponse(
        Long id,
        String name,
        String description,
        Instant scheduledAt,
        UserResponse creator,
        Instant createdAt
) {

    public static MeetingResponse from(Meeting meeting) {
        return new MeetingResponse(
                meeting.getId(),
                meeting.getName(),
                meeting.getDescription(),
                meeting.getScheduledAt(),
                new UserResponse(
                        meeting.getCreator().getId(),
                        meeting.getCreator().getName(),
                        meeting.getCreator().getEmail()
                ),
                meeting.getCreatedAt()
        );
    }
}
