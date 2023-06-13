package de.rieckpil.courses.book.management;

import java.io.IOException;
import java.time.Duration;

import com.nimbusds.jose.JOSEException;
import de.rieckpil.courses.AbstractIntegrationTest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static org.awaitility.Awaitility.given;

class BookSynchronizationListenerRefactoredIT extends AbstractIntegrationTest {

  private static final String ISBN = "9780596004651";
  private static String VALID_RESPONSE;

  static {
    try {
      VALID_RESPONSE =
          new String(
              BookSynchronizationListenerRefactoredIT.class
                  .getClassLoader()
                  .getResourceAsStream("stubs/openlibrary/success-" + ISBN + ".json")
                  .readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Autowired private SqsTemplate sqsTemplate;

  @Autowired private SqsAsyncClient sqsAsyncClient;

  @Autowired private WebTestClient webTestClient;

  @Test
  void shouldGetSuccessWhenClientIsAuthenticated() throws JOSEException {
    this.webTestClient
        .get()
        .uri("/api/books/reviews/statistics")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT())
        .exchange()
        .expectStatus()
        .is2xxSuccessful();
  }

  @Test
  void shouldReturnBookFromAPIWhenApplicationConsumesNewSyncRequest() {

    this.webTestClient
        .get()
        .uri("/api/books")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.size()")
        .isEqualTo(0);

    this.openLibraryStubs.stubForSuccessfulBookResponse(ISBN, VALID_RESPONSE);

    this.sqsAsyncClient.sendMessage(
        SendMessageRequest.builder()
            .queueUrl(
                this.sqsAsyncClient.getQueueUrl(r -> r.queueName(QUEUE_NAME)).join().queueUrl())
            .messageBody("""
    {
      "isbn": "%s"
    }
  """.formatted(ISBN))
            .build());

    given()
        .atMost(Duration.ofSeconds(5))
        .await()
        .untilAsserted(
            () -> {
              this.webTestClient
                  .get()
                  .uri("/api/books")
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody()
                  .jsonPath("$.size()")
                  .isEqualTo(1)
                  .jsonPath("$[0].isbn")
                  .isEqualTo(ISBN);
            });
  }
}
