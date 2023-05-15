package de.rieckpil.courses.book.management;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.test.sqs.SqsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.awaitility.Awaitility.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SqsTest(BookSynchronizationListener.class)
@Testcontainers(disabledWithoutDocker = true)
class BookSynchronizationListenerSliceNewTest {

  private static final Logger LOG =
      LoggerFactory.getLogger(BookSynchronizationListenerSliceTest.class);

  @Container
  static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.2"))
          .withServices(LocalStackContainer.Service.SQS)
          .withLogConsumer(new Slf4jLogConsumer(LOG));

  private static final String QUEUE_NAME = UUID.randomUUID().toString();
  private static final String ISBN = "9780596004651";

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("sqs.book-synchronization-queue", () -> QUEUE_NAME);
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.region.static", () -> localStack.getRegion());
    registry.add("spring.cloud.aws.endpoint", () -> localStack.getEndpointOverride(SQS).toString());
  }

  @Autowired private BookSynchronizationListener cut;

  @Autowired private SqsTemplate sqsTemplate;

  @MockBean private BookRepository bookRepository;

  @MockBean private OpenLibraryApiClient openLibraryApiClient;

  @Test
  void shouldConsumeMessageWhenPayloadIsCorrect() {
    sqsTemplate.send(QUEUE_NAME, new BookSynchronization(ISBN));

    when(bookRepository.findByIsbn(ISBN)).thenReturn(new Book());

    given()
        .await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(bookRepository).findByIsbn(ISBN));
  }
}
