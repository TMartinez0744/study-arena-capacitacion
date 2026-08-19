package edu.studyarena.training.service;

import edu.studyarena.training.dto.CreateMeetingRequest;
import edu.studyarena.training.dto.MeetingResponse;
import edu.studyarena.training.entity.Meeting;
import edu.studyarena.training.entity.User;
import edu.studyarena.training.exception.MeetingNotFoundException;
import edu.studyarena.training.exception.UserNotFoundException;
import edu.studyarena.training.repository.MeetingRepository;
import edu.studyarena.training.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

//Casos de uso de reuniones: crear, listar y ver el detalle
@Service
public class MeetingService {

    private static final int SUFFIX_BYTES = 4;
    private static final int MAX_SLUG_LENGTH = 40;
    private static final String FALLBACK_SLUG = "sala";

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    public MeetingService(MeetingRepository meetingRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    //Trae todas las reuniones ordenadas por fecha y las pasa al formato de respuesta
    public List<MeetingResponse> findAll() {
        return meetingRepository.findAllByOrderByScheduledAtAsc()
                .stream()
                .map(MeetingResponse::from)
                .toList();
    }

    public MeetingResponse findById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        return MeetingResponse.from(meeting);
    }

    //Crea la reunion poniendo como creador al usuario del token y generandole el nombre de sala
    public MeetingResponse create(CreateMeetingRequest request, String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Meeting meeting = new Meeting();
        meeting.setName(request.name());
        meeting.setDescription(request.description());
        meeting.setScheduledAt(request.scheduledAt());
        meeting.setCreator(creator);
        meeting.setRoomName(generateRoomName(request.name()));

        return MeetingResponse.from(meetingRepository.save(meeting));
    }

    private String generateRoomName(String meetingName) {
        String slug = slugify(meetingName);
        String roomName;

        do {
            roomName = slug + "-" + randomSuffix();
        } while (meetingRepository.existsByRoomName(roomName));

        return roomName;
    }

    //Convierte el nombre en algo que se pueda poner en una URL
    private String slugify(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-");

        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH);
        }

        slug = slug.replaceAll("(^-+|-+$)", "");

        return slug.isEmpty() ? FALLBACK_SLUG : slug;
    }

    //Cuatro bytes al azar para q el nombre de la sala no sea predecible
    private String randomSuffix() {
        byte[] bytes = new byte[SUFFIX_BYTES];
        random.nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }
}
