package de.rieckpil.courses.book.review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class BookReviewRequest {

  @NotEmpty private String reviewTitle;

  @NotEmpty private String reviewContent;

  @NotNull @PositiveOrZero private Integer rating;

  public BookReviewRequest(String reviewTitle, String reviewContent, Integer rating) {
    this.reviewTitle = reviewTitle;
    this.reviewContent = reviewContent;
    this.rating = rating;
  }

  public String getReviewTitle() {
    return reviewTitle;
  }

  public void setReviewTitle(String reviewTitle) {
    this.reviewTitle = reviewTitle;
  }

  public String getReviewContent() {
    return reviewContent;
  }

  public void setReviewContent(String reviewContent) {
    this.reviewContent = reviewContent;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }
}
