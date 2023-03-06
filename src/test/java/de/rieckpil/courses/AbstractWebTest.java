package de.rieckpil.courses;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import de.rieckpil.courses.initializer.WireMockInitializer;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.File;
import java.net.URI;
import java.time.Duration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ActiveProfiles("web-test")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractWebTest {

  protected static Logger LOG = LoggerFactory.getLogger(AbstractWebTest.class);

  public static DockerComposeContainer<?> environment =
    new DockerComposeContainer<>(new File("docker-compose.yml"))
      .withExposedService("database_1", 5432, Wait.forListeningPort())
      .withExposedService("keycloak_1", 8080, Wait.forHttp("/auth").forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(45)))
      .withExposedService("sqs_1", 9324, Wait.forListeningPort())
      .withLogConsumer("keycloak_1", new Slf4jLogConsumer(LOG))
      .withLogConsumer("database_1", new Slf4jLogConsumer(LOG))
      .withLogConsumer("sqs_1", new Slf4jLogConsumer(LOG))
      .withLocalCompose(true);

  @RegisterExtension
  static ScreenShooterExtension screenShooterExtension = new ScreenShooterExtension()
    .to("target/selenide-screenshots");

  static {
    environment.start();
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.endpoint", () -> "localhost:9324");
  }
}
