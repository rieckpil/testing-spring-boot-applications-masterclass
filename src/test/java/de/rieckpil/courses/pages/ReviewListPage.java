package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ReviewListPage {

  public ReviewListPage shouldContainExactlyOneReview(String reviewTitle, String reviewContent) {
    $("#all-reviews").click();
    $("#reviews").should(Condition.appear);
    $$("#reviews > div").shouldHaveSize(1);
    $("#review-0 .review-title").shouldHave(Condition.text(reviewTitle));
    $("#review-0 .review-content").shouldHave(Condition.text(reviewContent));
    return this;
  }
}
