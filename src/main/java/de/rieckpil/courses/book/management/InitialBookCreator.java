package de.rieckpil.courses.book.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("default")
public class InitialBookCreator implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(InitialBookCreator.class.getName());

  private final BookRepository bookRepository;
  private final QueueMessagingTemplate queueMessagingTemplate;
  private final String bookSynchronizationQueueName;

  public InitialBookCreator(BookRepository bookRepository,
                            QueueMessagingTemplate queueMessagingTemplate,
                            @Value("${sqs.book-synchronization-queue}") String bookSynchronizationQueueName) {
    this.bookRepository = bookRepository;
    this.queueMessagingTemplate = queueMessagingTemplate;
    this.bookSynchronizationQueueName = bookSynchronizationQueueName;
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("InitialBookCreator running ...");
    if (bookRepository.count() == 0) {
      LOG.info("Going to initialize first set of books");
      for (String isbn : List.of("9780321751041", "9780321160768", "9780596004651")) {
        queueMessagingTemplate.convertAndSend(bookSynchronizationQueueName, new BookSynchronization(isbn));
      }
    } else {
      LOG.info("No need to pre-populate books as database already contains some");
    }
  }
}
