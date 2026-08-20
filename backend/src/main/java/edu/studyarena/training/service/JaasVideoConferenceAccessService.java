package edu.studyarena.training.service;

import edu.studyarena.training.configuration.JitsiProperties;
import edu.studyarena.training.dto.VideoConferenceAccess;
import edu.studyarena.training.entity.Meeting;
import edu.studyarena.training.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

//Firma el token q JaaS le pide al usuario para dejarlo entrar a la sala
@Service
public class JaasVideoConferenceAccessService implements VideoConferenceAccessService {

    private final JitsiProperties properties;

    //Se carga la 1era vez q alguien entra a una llamada
    private PrivateKey privateKey;

    public JaasVideoConferenceAccessService(JitsiProperties properties) {
        this.properties = properties;
    }

    //Arma token: quien entra, a que sala y hasta q hora
    @Override
    public VideoConferenceAccess createAccess(Meeting meeting, User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.tokenTtl());
        boolean moderator = meeting.getCreator().getId().equals(user.getId());

        String token = Jwts.builder()
                .header().keyId(properties.kid()).and()
                .claim("aud", "jitsi")
                .claim("iss", "chat")
                .claim("sub", properties.appId())
                .claim("room", meeting.getRoomName())
                .claim("context", context(user, moderator))
                .notBefore(Date.from(now.minusSeconds(10)))
                .expiration(Date.from(expiresAt))
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();

        return new VideoConferenceAccess(
                properties.domain(),
                properties.appId() + "/" + meeting.getRoomName(),
                token,
                expiresAt
        );
    }

    //Datos que Jitsi muestra en la llamada
    private Map<String, Object> context(User user, boolean moderator) {
        return Map.of(
                "user", Map.of(
                        "id", String.valueOf(user.getId()),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "moderator", moderator
                ),
                "features", Map.of(
                        "recording", false,
                        "livestreaming", false,
                        "transcription", false
                ),
                "room", Map.of("regex", false)
        );
    }

    private synchronized PrivateKey privateKey() {
        if (privateKey == null) {
            privateKey = loadPrivateKey();
        }

        return privateKey;
    }

    private PrivateKey loadPrivateKey() {
        try (InputStream input = properties.privateKey().getInputStream()) {
            String pem = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            String base64 = pem
                    .replaceAll("-----BEGIN (RSA )?PRIVATE KEY-----", "")
                    .replaceAll("-----END (RSA )?PRIVATE KEY-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", "");

            byte[] encoded = Base64.getDecoder().decode(base64);

            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (IOException | GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo leer la clave privada de Jitsi", ex);
        }
    }
}
