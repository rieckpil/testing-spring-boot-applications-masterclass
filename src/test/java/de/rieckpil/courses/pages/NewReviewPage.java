package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class NewReviewPage {

  public NewReviewPage submitReview(
      String reviewTitle, String reviewContent, int selectedBook, int rating) {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click(usingJavaScript());

    $("#review-submit").should(Condition.appear);
    $("#book-selection").should(Condition.enabled);

    Actions actions = new Actions(getWebDriver());
    actions.moveToElement($("#book-selection").getWrappedElement()).click().perform();
    $("[role='option']").should(Condition.appear);
    actions
        .moveToElement($$("[role='option']").get(selectedBook).getWrappedElement())
        .click()
        .perform();
    actions
        .moveToElement($$("#book-rating label").get(rating + 1).getWrappedElement())
        .click()
        .perform();

    // sendKeysToElement reliably fires browser input events that React's onChange handles
    $("#review-title").sendKeys(reviewTitle);
    $("#review-content").sendKeys(reviewContent);

    $("#review-submit").click(usingJavaScript());
    $("[role='alert']").should(Condition.appear);
    return this;
  }
}
