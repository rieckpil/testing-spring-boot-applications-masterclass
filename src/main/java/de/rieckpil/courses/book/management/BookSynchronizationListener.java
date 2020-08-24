package de.rieckpil.courses.book.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class BookSynchronizationListener {

  private static final Logger LOG = LoggerFactory.getLogger(BookSynchronizationListener.class.getName());

  private final BookRepository bookRepository;
  private final OpenLibraryApiClient openLibraryApiClient;

  public BookSynchronizationListener(BookRepository bookRepository,
                                     OpenLibraryApiClient openLibraryApiClient) {
    this.bookRepository = bookRepository;
    this.openLibraryApiClient = openLibraryApiClient;
  }

  @SqsListener(value = "${sqs.book-synchronization-queue}")
  public void consumeBookUpdates(BookSynchronization bookSynchronization) {

    String isbn = bookSynchronization.getIsbn();
    LOG.info("Incoming book update for isbn '{}'", isbn);

    if (isbn.length() != 13) {
      LOG.warn("Incoming isbn for book is not 13 characters long, rejecting it");
      return;
    }

    if (bookRepository.findByIsbn(isbn) != null) {
      LOG.debug("Book with isbn '{}' is already present, rejecting it", isbn);
      return;
    }

    Book book = openLibraryApiClient.fetchMetadataForBook(isbn);
    book = bookRepository.save(book);
    System.out.println(book);

    LOG.info("Successfully stored new book '{}'", book);
  }

}
