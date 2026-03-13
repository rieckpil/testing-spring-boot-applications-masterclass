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
    $("#book-selection").shouldNotHave(Condition.cssClass("loading"));

    Actions actions = new Actions(getWebDriver());
    actions.moveToElement($("#book-selection").getWrappedElement()).click().perform();
    $(".visible .menu").should(Condition.appear);
    actions
        .moveToElement($$(".visible .menu > div").get(selectedBook).getWrappedElement())
        .click()
        .perform();
    actions.moveToElement($$("#book-rating > i").get(rating).getWrappedElement()).click().perform();

    typeValue("#review-title", reviewTitle);
    typeValue("#review-content", reviewContent);

    $("#review-submit").click(usingJavaScript());
    $(".ui .success").should(Condition.appear);
    return this;
  }

  private void typeValue(String cssSelector, String value) {
    // Use Actions.sendKeys to generate real keyboard events that React's onChange handles reliably
    // across all Chrome/Firefox versions. The JS native-value-setter approach breaks on newer
    // Chrome (145+) due to changes in how synthetic events interact with React's event system.
    new Actions(getWebDriver()).click($(cssSelector).getWrappedElement()).sendKeys(value).perform();
  }
}
