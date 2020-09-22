package de.rieckpil.courses;

import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.stubs.OpenLibraryStubs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("default")
class ApplicationIT extends AbstractIntegrationTest {

  @Autowired
  private OpenLibraryStubs openLibraryStubs;

  @Autowired
  private BookRepository bookRepository;

  @Test
  void shouldLoadContextAndPrepopulateBooksWhenProfileIsDefault() throws IOException {
    this.openLibraryStubs.stubForSuccessfulBookResponse("9780321751041", getValidResponse("9780321751041"));
    this.openLibraryStubs.stubForSuccessfulBookResponse("9780321160768", getValidResponse("9780321160768"));
    this.openLibraryStubs.stubForSuccessfulBookResponse("9780596004651", getValidResponse("9780596004651"));

    given()
      .await()
      .atMost(5, SECONDS)
      .untilAsserted(() -> assertEquals(3, bookRepository.count()));
  }

  private String getValidResponse(String isbn) throws IOException {
    return new String(ApplicationIT.class
      .getClassLoader()
      .getResourceAsStream("stubs/openlibrary/success-" + isbn + ".json")
      .readAllBytes());
  }
}
