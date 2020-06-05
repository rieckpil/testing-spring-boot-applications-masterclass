package de.rieckpil.courses.book.review;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class BadReviewQualityException extends RuntimeException {
  public BadReviewQualityException(String message) {
    super(message);
  }
}
