package de.rieckpil.courses.book.management;

import com.nimbusds.jose.JOSEException;
import de.rieckpil.courses.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.given;
import static org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

class BookSynchronizationListenerRefactoredIT extends AbstractIntegrationTest {

  private static final String ISBN = "9780596004651";
  private static String VALID_RESPONSE;

  static {
    try (InputStream is = BookSynchronizationListenerRefactoredIT.class
      .getClassLoader()
      .getResourceAsStream("stubs/openlibrary/success-" + ISBN + ".json")) {
      byte[] payload = is != null ? is.readAllBytes() : new byte[]{};
      VALID_RESPONSE = new String(payload);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private BookRepository bookRepository;

  @Test
  void shouldGetSuccessWhenClientIsAuthenticated() throws JOSEException {
    this.webTestClient
      .get()
      .uri("/api/books/reviews/statistics")
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT())
      .exchange()
      .expectStatus().is2xxSuccessful();
  }

  @Test
  void shouldReturnBookFromAPIWhenApplicationConsumesNewSyncRequest() {
    assertNonExistingRecord();

    this.openLibraryStubs.stubForSuccessfulBookResponse(ISBN, VALID_RESPONSE);

    this.queueMessagingTemplate.send(QUEUE_NAME, new GenericMessage<>(
      """
          {
            "isbn": "%s"
          }
        """.formatted(ISBN), Map.of("contentType", "application/json")));

    given()
      .atMost(Duration.ofSeconds(5))
      .await()
      .untilAsserted(this::assertExistingRecord);
  }

  private void assertNonExistingRecord() {
    fetchOpenLibraryResponse()
      .jsonPath("$.size()").isEqualTo(0);
  }

  private void assertExistingRecord() {
    fetchOpenLibraryResponse()
      .jsonPath("$.size()").isEqualTo(1)
      .jsonPath("$[0].isbn").isEqualTo(ISBN);
  }

  private BodyContentSpec fetchOpenLibraryResponse() {
    return this.webTestClient
      .get()
      .uri("/api/books")
      .exchange()
      .expectStatus().isOk()
      .expectBody();
  }
}
