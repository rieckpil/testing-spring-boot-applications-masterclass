package de.rieckpil.courses.book.review;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ReviewVerifierTest {

  private ReviewVerifier reviewVerifier;

  @BeforeEach
  public void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @Test
  void shouldFailWhenReviewContainsSwearWord() {
    String review = "This book is shit";
    System.out.println("Testing a review");

    boolean result = reviewVerifier.doesMeetQualityStandards(review);
    assertFalse(result, "ReviewVerifier did not detect swear word");
  }

}
