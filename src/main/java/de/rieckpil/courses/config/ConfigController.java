package de.rieckpil.courses.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

  private final String keycloakAuthServerUrl;

  public ConfigController(@Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl) {
    this.keycloakAuthServerUrl = keycloakAuthServerUrl;
  }

  @GetMapping("/api/config")
  public Map<String, String> config() {
    return Map.of("keycloakUrl", keycloakAuthServerUrl);
  }
}
