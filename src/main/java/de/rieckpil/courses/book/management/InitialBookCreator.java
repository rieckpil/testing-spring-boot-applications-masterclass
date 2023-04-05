package de.rieckpil.courses.book.management;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class InitialBookCreator {

  private static final Logger LOG = LoggerFactory.getLogger(InitialBookCreator.class.getName());

  private final BookRepository bookRepository;
  private final SqsTemplate sqsTemplate;
  private final String bookSynchronizationQueueName;

  public InitialBookCreator(
      BookRepository bookRepository,
      SqsTemplate sqsTemplate,
      @Value("${sqs.book-synchronization-queue}") String bookSynchronizationQueueName) {
    this.bookRepository = bookRepository;
    this.sqsTemplate = sqsTemplate;
    this.bookSynchronizationQueueName = bookSynchronizationQueueName;
  }

  @EventListener
  public void initialize(ApplicationReadyEvent event) {
    LOG.info("InitialBookCreator running ...");
    if (bookRepository.count() == 0) {
      LOG.info("Going to initialize first set of books");
      for (String isbn : List.of("9780321751041", "9780321160768", "9780596004651")) {
        // enforce uniqueness of messages as messages might get stuck in the mock SQS queue
        // otheriwse
        Map<String, Object> messageHeaders =
            Map.of("x-custom-header", UUID.randomUUID().toString());
        sqsTemplate.send(
            bookSynchronizationQueueName,
            new GenericMessage<>(new BookSynchronization(isbn), messageHeaders));
      }
    } else {
      LOG.info("No need to pre-populate books as database already contains some");
    }
  }
}
