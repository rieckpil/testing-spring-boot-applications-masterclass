package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class NewReviewPage {

  public NewReviewPage submitReview(String reviewTitle, String reviewContent, int rating) {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click(usingJavaScript());

    $("#review-submit").should(Condition.appear);
    // Wait for the books dropdown to finish loading (first book is preselected automatically)
    $("#book-selection").should(Condition.enabled);

    $$("#book-rating label").get(rating + 1).click(usingJavaScript());

    $("#review-title").sendKeys(reviewTitle);
    $("#review-content").sendKeys(reviewContent);

    $("#review-submit").click(usingJavaScript());
    $("[role='alert']").should(Condition.appear);
    return this;
  }
}
