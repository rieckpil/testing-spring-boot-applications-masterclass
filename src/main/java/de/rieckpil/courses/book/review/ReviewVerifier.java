package de.rieckpil.courses.book.review;

import org.springframework.stereotype.Service;

@Service
public class ReviewVerifier {

  public boolean doesNotContainSwearWords(String review) {
    return false;
  }

  public boolean doesMeetQualityStandards(String review) {
    return true;
  }

}
