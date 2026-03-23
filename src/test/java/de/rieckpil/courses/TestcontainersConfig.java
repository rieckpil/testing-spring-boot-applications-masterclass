package de.rieckpil.courses;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

  private static final Logger LOG = LoggerFactory.getLogger(TestcontainersConfig.class);

  @Bean
  public DynamicPropertyRegistrar properties(
      LocalStackContainer localStackContainer, GenericContainer<?> keycloakContainer) {
    return (properties) -> {
      properties.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
      properties.add("spring.cloud.aws.credentials.access-key", () -> "bar");
      properties.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
      properties.add("clients.open-library.base-url", () -> "https://openlibrary.org");

      properties.add(
          "spring.security.oauth2.resourceserver.jwt.issuer-uri",
          () ->
              String.format(
                  "http://%s:%d/auth/realms/spring",
                  keycloakContainer.getHost(), keycloakContainer.getMappedPort(8080)));
      properties.add(
          "keycloak.auth-server-url",
          () ->
              String.format(
                  "http://%s:%d/auth",
                  keycloakContainer.getHost(), keycloakContainer.getMappedPort(8080)));
    };
  }

  @Bean
  @ServiceConnection
  PostgreSQLContainer database() {
    return new PostgreSQLContainer("postgres:17.2")
        .withDatabaseName("test")
        .withUsername("duke")
        .withPassword("s3cret");
  }

  @Bean
  public LocalStackContainer localStack() {
    return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.9.2"));
  }

  @Bean
  GenericContainer<?> keycloakContainer() {
    return new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:26.5.6"))
        .withCommand("start-dev", "--http-relative-path", "/auth", "--import-realm")
        .withEnv("KEYCLOAK_DB", "dev-file")
        .withEnv("KEYCLOAK_ADMIN", "keycloak")
        .withEnv("KEYCLOAK_ADMIN_PASSWORD", "keycloak")
        .withEnv("KEYCLOAK_FEATURES", "scripts")
        .withFileSystemBind("./tmp", "/opt/keycloak/data/import", BindMode.READ_ONLY)
        .withExposedPorts(8080)
        .waitingFor(Wait.forHttp("/auth").forStatusCode(200))
        .withLogConsumer(new Slf4jLogConsumer(LOG))
        .withStartupTimeout(Duration.ofMinutes(2));
  }
}
