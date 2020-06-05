package de.rieckpil.courses.book.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewVerifierTest {

  private ReviewVerifier cut;

  @BeforeEach
  public void setup() {
    this.cut = new ReviewVerifier();
  }

  @Test
  public void testQualityStandard() {
    assertTrue(cut.doesMeetQualityStandards("I am good person. I find this nice you good test"), "explain");
    assertFalse(cut.doesMeetQualityStandards("I am good person. I find this nice I am good I like this really I hate this I good"), "explain");
    assertFalse(cut.doesMeetQualityStandards("I am"), "explain");
    assertFalse(cut.doesMeetQualityStandards("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt"), "explain");
  }

}
