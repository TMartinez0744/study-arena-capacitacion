package edu.studyarena.training;

import edu.studyarena.training.dto.AuthResponse;
import edu.studyarena.training.dto.CreateMeetingRequest;
import edu.studyarena.training.dto.LoginRequest;
import edu.studyarena.training.dto.MeetingResponse;
import edu.studyarena.training.dto.RegisterRequest;
import edu.studyarena.training.repository.MeetingRepository;
import edu.studyarena.training.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//como requests.http pero automatico
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MeetingApiTest {

    private static final String EMAIL = "trini@mail.com";
    private static final String PASSWORD = "password123";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    private RestTestClient client;
    private String token;

    //antes de cada test deja la base vacia, registra el usuario y consigue un token nuevo
    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        meetingRepository.deleteAll();
        userRepository.deleteAll();

        client.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RegisterRequest("Trini", EMAIL, PASSWORD))
                .exchange()
                .expectStatus().isCreated();

        AuthResponse auth = client.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(EMAIL, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(auth);
        token = auth.token();
    }

    //con token valido
    @Test
    void shouldCreateMeeting() {
        MeetingResponse created = client.post().uri("/api/meetings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newMeeting("Repaso de Algebra"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MeetingResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        assertEquals("Repaso de Algebra", created.name());
        assertEquals(EMAIL, created.creator().email());
        assertEquals(1, meetingRepository.count());
    }


    @Test
    void shouldRejectMeetingWithoutToken() {
        client.post().uri("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newMeeting("Sin token"))
                .exchange()
                .expectStatus().isUnauthorized();

        assertEquals(0, meetingRepository.count());
    }

    //id que no existe
    @Test
    void shouldReturnNotFoundForUnknownMeeting() {
        client.get().uri("/api/meetings/99999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    //el mismo test corre una vez por cada fila del CsvSource
    @ParameterizedTest
    @CsvSource({
            "'', BAD_REQUEST",
            "'   ', BAD_REQUEST"
    })
    void shouldRejectInvalidMeetingNames(String name, HttpStatus expectedStatus) {
        client.post().uri("/api/meetings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newMeeting(name))
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);

        assertEquals(0, meetingRepository.count());
    }

    private CreateMeetingRequest newMeeting(String name) {
        return new CreateMeetingRequest(
                name,
                "descripcion de prueba",
                Instant.now().plus(Duration.ofDays(1))
        );
    }
}
