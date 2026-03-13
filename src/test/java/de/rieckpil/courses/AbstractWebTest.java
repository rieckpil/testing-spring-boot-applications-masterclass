package de.rieckpil.courses;

import java.io.File;
import java.time.Duration;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import de.rieckpil.courses.initializer.WireMockInitializer;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("web-test")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractWebTest {

  protected static Logger LOG = LoggerFactory.getLogger(AbstractWebTest.class);

  // this requires Docker Compose v2, for v1, consider DockerComposeContainer
  public static ComposeContainer environment =
      new ComposeContainer(new File("docker-compose.yml"))
          .withExposedService("database-1", 5432, Wait.forListeningPort())
          .withExposedService(
              "keycloak-1",
              8080,
              Wait.forHttp("/auth").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(90)))
          .withExposedService("sqs-1", 9324, Wait.forListeningPort())
          .withLogConsumer("keycloak-1", new Slf4jLogConsumer(LOG))
          .withLogConsumer("database-1", new Slf4jLogConsumer(LOG))
          .withLogConsumer("sqs-1", new Slf4jLogConsumer(LOG));

  @RegisterExtension
  static ScreenShooterExtension screenShooterExtension =
      new ScreenShooterExtension().to("target/selenide-screenshots");

  static {
    environment.start();
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.endpoint", () -> "http://localhost:9324");
  }
}
