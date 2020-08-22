package de.rieckpil.courses;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import de.rieckpil.courses.book.management.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest
@Import(AwsTestConfig.class)
@Testcontainers(disabledWithoutDocker = true)
class ApplicationTests {

  @Container
  static PostgreSQLContainer container = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12")
    .withDatabaseName("test")
    .withUsername("duke")
    .withPassword("s3cret")
    .withReuse(true);

  @Container
  static LocalStackContainer localStack = new LocalStackContainer("0.10.0")
    .withServices(SQS)
    .withEnv("DEFAULT_REGION", "eu-central-1");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.datasource.username", container::getUsername);
  }

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "test-default");
  }

  @Autowired
  private BookRepository bookRepository;

  @Test
  void contextLoads() {
    given()
      .ignoreException(AmazonS3Exception.class)
      .await()
      .atMost(5, SECONDS)
      .untilAsserted(() -> assertEquals(3, bookRepository.count()));
  }

}
