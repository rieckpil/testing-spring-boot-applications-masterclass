package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

public class NewReviewPage {

  public NewReviewPage submitReview(String reviewTitle, String reviewContent, int selectedBook, int rating) {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click();

    $("#review-submit").should(Condition.appear);
    $("#book-selection").click();
    $$(".visible .menu > div").get(selectedBook).click();
    $$("#book-rating > i").get(rating).click();

    $("#review-title").val(reviewTitle);
    $("#review-content").val(reviewContent);

    $("#review-submit").click();
    $(".ui .success").should(Condition.appear);
    return this;
  }
}
