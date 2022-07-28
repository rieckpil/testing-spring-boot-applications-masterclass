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

  @Test
  void shouldReturnCreatedReviewWhenBookExistsAndReviewHasGoodQuality() throws JOSEException {
  }

  @Test
  void shouldReturnReviewStatisticWhenMultipleReviewsForBookFromDifferentUsersExist() throws JOSEException {
  }
}
