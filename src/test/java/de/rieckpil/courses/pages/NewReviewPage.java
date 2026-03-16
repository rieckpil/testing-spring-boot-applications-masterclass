package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class NewReviewPage {

  public NewReviewPage submitReview(
      String reviewTitle, String reviewContent, int selectedBook, int rating) {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click(usingJavaScript());

    $("#review-submit").should(Condition.appear);
    $("#book-selection").should(Condition.enabled);

    $("#book-selection").click();
    $("[role='option']").should(Condition.appear);
    $$("[role='option']").get(selectedBook).click(usingJavaScript());
    $$("#book-rating label").get(rating + 1).click(usingJavaScript());

    // sendKeysToElement reliably fires browser input events that React's onChange handles
    $("#review-title").sendKeys(reviewTitle);
    $("#review-content").sendKeys(reviewContent);

    $("#review-submit").click(usingJavaScript());
    $("[role='alert']").should(Condition.appear);
    return this;
  }
}
