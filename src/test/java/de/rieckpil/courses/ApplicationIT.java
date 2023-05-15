package de.rieckpil.courses;

import java.io.IOException;
import java.util.UUID;

import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.initializer.DefaultBookStubsInitializer;
import de.rieckpil.courses.initializer.WireMockInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles({"default", "integration-test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class, DefaultBookStubsInitializer.class})
class ApplicationIT {

  @Container
  static PostgreSQLContainer<?> database =
      new PostgreSQLContainer<>("postgres:15.2")
          .withDatabaseName("test")
          .withUsername("duke")
          .withPassword("s3cret");

  @Container
  static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.2"))
          .withServices(SQS);
  // can be removed with version 0.12.17 as LocalStack now has multi-region support
  // https://docs.localstack.cloud/localstack/configuration/#deprecated
  // .withEnv("DEFAULT_REGION", "eu-central-1")

  protected static final String QUEUE_NAME = UUID.randomUUID().toString();

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.password", database::getPassword);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("sqs.book-synchronization-queue", () -> QUEUE_NAME);
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.endpoint", () -> localStack.getEndpointOverride(SQS));
  }

  @Autowired private BookRepository bookRepository;

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
  }

  @Test
  void shouldLoadContextAndPrepopulateThreeBooksWhenProfileIsDefault() {
    given().await().atMost(5, SECONDS).untilAsserted(() -> assertEquals(3, bookRepository.count()));
  }
}
