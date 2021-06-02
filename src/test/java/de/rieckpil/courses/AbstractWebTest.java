package de.rieckpil.courses;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import de.rieckpil.courses.initializer.WireMockInitializer;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@ActiveProfiles("web-test")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractWebTest {

  public static DockerComposeContainer<?> environment =
    new DockerComposeContainer<>(new File("docker-compose.yml"))
      .withExposedService("database_1", 5432, Wait.forListeningPort())
      .withExposedService("keycloak_1", 8080, Wait.forHttp("/auth").forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(30)))
      .withExposedService("sqs_1", 9324, Wait.forListeningPort());

  @RegisterExtension
  static ScreenShooterExtension screenShooterExtension = new ScreenShooterExtension()
    .to("target/selenide-screenshots");

  static {
    environment.start();
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
      return AmazonSQSAsyncClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar")))
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:9324", "eu-central-1"))
        .build();
    }
  }
}
