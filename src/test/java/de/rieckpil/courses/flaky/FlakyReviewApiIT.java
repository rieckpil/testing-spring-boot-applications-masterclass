package de.rieckpil.courses.flaky;

import java.util.concurrent.TimeUnit;

import com.nimbusds.jose.JOSEException;
import de.rieckpil.courses.AbstractIntegrationTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intentionally flaky integration test to experiment with TestLens flaky test detection. The tight
 * latency assertion depends on machine load and JVM warmup. Do not fix or stabilize it, the
 * non-determinism is the point.
 */
class FlakyReviewApiIT extends AbstractIntegrationTest {

  private static final String ISBN = "9780596004651";

  @Autowired private WebTestClient webTestClient;

  @Autowired private BookRepository bookRepository;

  @BeforeEach
  void setup() {
    Book book = new Book();
    book.setPublisher("Duke Inc.");
    book.setIsbn(ISBN);
    book.setPages(42L);
    book.setTitle("Joyful testing with Spring Boot");
    book.setDescription("Writing unit and integration tests for Spring Boot applications");
    book.setAuthor("rieckpil");
    book.setThumbnailUrl(
        "https://rieckpil.de/wp-content/uploads/2020/08/tsbam_introduction_thumbnail-585x329.png.webp");
    book.setGenre("Software Development");

    this.bookRepository.save(book);
  }

  @Test
  void shouldCreateReviewWithinTightLatencyBudget() throws JOSEException {

    String reviewPayload =
        """
      {
        "reviewTitle" : "Great book with lots of tips & tricks",
        "reviewContent" : "I can really recommend reading this book. It includes up-to-date library versions and real-world examples",
        "rating": 4
      }
      """;

    String validJWT = getSignedJWT();
    long latencyBudgetInMilliseconds = 300;

    long startTime = System.nanoTime();

    this.webTestClient
        .post()
        .uri("/api/books/{isbn}/reviews", ISBN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validJWT)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(reviewPayload)
        .exchange()
        .expectStatus()
        .isCreated();

    long elapsedInMilliseconds = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

    assertTrue(
        elapsedInMilliseconds <= latencyBudgetInMilliseconds,
        "Review creation took "
            + elapsedInMilliseconds
            + " ms but the latency budget is "
            + latencyBudgetInMilliseconds
            + " ms");
  }
}
