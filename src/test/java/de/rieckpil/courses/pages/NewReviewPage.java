package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
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

    setReactInputValue("#review-title", reviewTitle);
    setReactInputValue("#review-content", reviewContent);

    $("#review-submit").click(usingJavaScript());
    $(".ui .success").should(Condition.appear);
    return this;
  }

  private void setReactInputValue(String cssSelector, String value) {
    executeJavaScript(
        "var el = document.querySelector(arguments[0]);"
            + "var proto = el.tagName === 'TEXTAREA'"
            + "  ? window.HTMLTextAreaElement.prototype"
            + "  : window.HTMLInputElement.prototype;"
            + "Object.getOwnPropertyDescriptor(proto, 'value').set.call(el, arguments[1]);"
            + "el.dispatchEvent(new Event('input', { bubbles: true }));",
        cssSelector,
        value);
  }
}
