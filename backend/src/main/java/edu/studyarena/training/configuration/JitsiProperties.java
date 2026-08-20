package edu.studyarena.training.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

//los datos q llegan por variables de entorno
@ConfigurationProperties(prefix = "jitsi")
public record JitsiProperties(
        String appId,
        String kid,
        String domain,
        Resource privateKey,
        long tokenTtlSeconds
) {

    public Duration tokenTtl() {
        return Duration.ofSeconds(tokenTtlSeconds);
    }
}
