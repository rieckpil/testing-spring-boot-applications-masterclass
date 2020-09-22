package de.rieckpil.courses.book.review;

import com.nimbusds.jose.JOSEException;
import de.rieckpil.courses.AbstractIntegrationTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

public class ReviewControllerIT extends AbstractIntegrationTest {

  private static final String ISBN = "9780596004651";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private BookRepository bookRepository;

  @BeforeEach
  public void setup() {
    Book book = new Book();
    book.setPublisher("Duke Inc.");
    book.setIsbn(ISBN);
    book.setPages(42L);
    book.setTitle("Joyful testing with Spring Boot");
    book.setDescription("Writing unit and integration tests for Spring Boot applications");
    book.setAuthor("rieckpil");
    book.setThumbnailUrl("https://rieckpil.de/wp-content/uploads/2020/08/tsbam_introduction_thumbnail-585x329.png.webp");
    book.setGenre("Software Development");

    this.bookRepository.save(book);
  }

  @Test
  public void shouldReturnCreatedReviewWhenBookExistsAndReviewHasGoodQuality() throws JOSEException {

    String reviewPayload = """
      {
        "reviewTitle" : "Great book with lots of tips & tricks",
        "reviewContent" : "I can really recommend reading this book. It includes up-to-date library versions and real-world examples",
        "rating": 4
      }
      """;

    String validJWT = getSignedJWT();

    HttpHeaders responseHeaders = this.webTestClient
      .post()
      .uri("/api/books/{isbn}/reviews", ISBN)
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + validJWT)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(reviewPayload)
      .exchange()
      .expectStatus().isCreated()
      .returnResult(ResponseEntity.class)
      .getResponseHeaders();

    this.webTestClient
      .get()
      .uri(responseHeaders.getLocation())
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + validJWT)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.reviewTitle").isEqualTo("Great book with lots of tips & tricks")
      .jsonPath("$.rating").isEqualTo(4)
      .jsonPath("$.bookIsbn").isEqualTo(ISBN);
  }

  @Test
  public void shouldReturnReviewStatisticWhenMultipleReviewsForBookFromDifferentUsersExist() throws JOSEException {

    this.webTestClient
      .post()
      .uri("/api/books/{isbn}/reviews", ISBN)
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT("mike", "mike@spring.io"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "reviewTitle" : "Great book with lots of tips & tricks",
          "reviewContent" : "I can really recommend reading this book. It includes up-to-date library versions and real-world examples",
          "rating": 5
        }
        """)
      .exchange()
      .expectStatus().isCreated();

    this.webTestClient
      .post()
      .uri("/api/books/{isbn}/reviews", ISBN)
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT("duke", "duke@spring.io"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "reviewTitle" : "This book is okay",
          "reviewContent" : "I can recommend reading this book for everyone who wants to get started with testing",
          "rating": 3
        }
        """)
      .exchange()
      .expectStatus().isCreated();

    this.webTestClient
      .post()
      .uri("/api/books/{isbn}/reviews", ISBN)
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT("mandy", "mandy@spring.io"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""
        {
          "reviewTitle" : "This book is great",
          "reviewContent" : "Good content, great example and most of all up-to-date frameworks and libraries",
          "rating": 4
        }
        """)
      .exchange()
      .expectStatus().isCreated();

    this.webTestClient
      .get()
      .uri("/api/books/reviews/statistics")
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT())
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.size()").isEqualTo(1)
      .jsonPath("$[0].isbn").isEqualTo(ISBN)
      .jsonPath("$[0].ratings").isEqualTo(3)
      .jsonPath("$[0].avg").isEqualTo(4.00);
  }


}
