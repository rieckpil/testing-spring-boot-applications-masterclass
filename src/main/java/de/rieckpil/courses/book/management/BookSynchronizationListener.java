package de.rieckpil.courses.book.management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookSynchronizationListener implements CommandLineRunner {

  private final BookRepository bookRepository;
  private final OpenLibraryApiClient openLibraryApiClient;

  public BookSynchronizationListener(BookRepository bookRepository, OpenLibraryApiClient openLibraryApiClient) {
    this.bookRepository = bookRepository;
    this.openLibraryApiClient = openLibraryApiClient;
  }

  @Override
  public void run(String... args) {
    List<String> isbnList = List.of("9780321751041", "9780321160768", "9780596004651");
    if (bookRepository.findAll().isEmpty()) {
      isbnList.forEach(isbn -> {
        Book book = openLibraryApiClient.fetchMetadataForBook(isbn);
        book = bookRepository.save(book);
        System.out.println(book);
      });
    }
  }
}
