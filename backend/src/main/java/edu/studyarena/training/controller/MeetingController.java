package edu.studyarena.training.controller;

import edu.studyarena.training.dto.CreateMeetingRequest;
import edu.studyarena.training.dto.MeetingResponse;
import edu.studyarena.training.dto.VideoConferenceAccess;
import edu.studyarena.training.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> findAll() {
        return ResponseEntity.ok(meetingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MeetingResponse> create(
            @Valid @RequestBody CreateMeetingRequest request,
            Authentication authentication
    ) {
        MeetingResponse meeting = meetingService.create(request, authentication.getName());

        return ResponseEntity
                .created(URI.create("/api/meetings/" + meeting.id()))
                .body(meeting);
    }

    //Se pide justo antes de entrar a la videollamada, porque el token dura pocos minutos
    @PostMapping("/{id}/access")
    public ResponseEntity<VideoConferenceAccess> createAccess(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(meetingService.createAccess(id, authentication.getName()));
    }
}
