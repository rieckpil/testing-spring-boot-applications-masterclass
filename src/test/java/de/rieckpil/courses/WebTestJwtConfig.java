package de.rieckpil.courses;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@TestConfiguration
public class WebTestJwtConfig {

  // Validates JWT signatures using Keycloak's public keys but skips issuer (iss) claim
  // validation. The browser accesses Keycloak via host.testcontainers.internal, so the
  // iss claim differs from the localhost URL that Spring Boot uses — they can never match.
  @Bean
  @Primary
  public JwtDecoder webTestJwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(
            "http://localhost:8888/auth/realms/spring/protocol/openid-connect/certs")
        .build();
  }
}
