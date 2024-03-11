package de.rieckpil.courses.book.review;

import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class ReviewVerifier {

  private boolean doesNotContainSwearWords(String review) {
    return !review.contains("shit");
  }

  public boolean doesMeetQualityStandards(String review) {

    if (review.contains("Lorem ipsum")) {
      return false;
    }

    String[] words = review.split(" ");

    if (Arrays.stream(words).filter(s -> s.equalsIgnoreCase("I")).count() >= 5) {
      return false;
    }

    if (Arrays.stream(words).filter(s -> s.equalsIgnoreCase("good")).count() >= 3) {
      return false;
    }

    if (words.length <= 10) {
      return false;
    }

    return doesNotContainSwearWords(review);
  }
}
