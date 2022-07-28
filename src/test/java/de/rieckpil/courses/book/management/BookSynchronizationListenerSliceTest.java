package de.rieckpil.courses.book.management;

import java.io.IOException;
import java.util.UUID;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ExtendWith(SpringExtension.class)
@Import(BookSynchronizationListener.class)
@ImportAutoConfiguration(MessagingAutoConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class BookSynchronizationListenerSliceTest {

  private static final Logger LOG = LoggerFactory.getLogger(BookSynchronizationListenerSliceTest.class);

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.13.3"))
    .withServices(LocalStackContainer.Service.SQS)
    // can be removed with version 0.12.17 as LocalStack now has multi-region support https://docs.localstack.cloud/localstack/configuration/#deprecated
    // .withEnv("DEFAULT_REGION", "eu-central-1")
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
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public AmazonSQSAsync amazonSQS() {
      return AmazonSQSAsyncClientBuilder.standard()
        .withCredentials(localStack.getDefaultCredentialsProvider())
        .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
        .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQS) {
      return new QueueMessagingTemplate(amazonSQS);
    }
  }

  @Autowired
  private BookSynchronizationListener cut;

  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;

  @Autowired
  private SimpleMessageListenerContainer messageListenerContainer;

  @MockBean
  private BookRepository bookRepository;

  @MockBean
  private OpenLibraryApiClient openLibraryApiClient;

  @Test
  void shouldStartSQS() {
  }

  @Test
  void shouldConsumeMessageWhenPayloadIsCorrect() {
  }
}
