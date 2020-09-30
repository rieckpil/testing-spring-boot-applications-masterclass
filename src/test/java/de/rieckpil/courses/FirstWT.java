package de.rieckpil.courses;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;

import static com.codeborne.selenide.Selenide.*;

public class FirstWT {

  static BrowserWebDriverContainer webDriverContainer = new BrowserWebDriverContainer()
    .withCapabilities(new ChromeOptions().setHeadless(false));

  static {
    webDriverContainer.start();
  }

  private int port = 8080;

  @Test
  public void startEnvironment() throws InterruptedException {
    // this.webDriverContainer.getWebDriver().get("http://host.docker.internal:" + port + "/");
    // this.webDriverContainer.getWebDriver().get(webDriverContainer.getTestHostIpAddress() + port + "/");
    open("http://172.17.0.1:" + port + "/");
    $("#login").click();

    $("#kc-login").should(Condition.appear);
    $("#username").val("duke");
    $("#password").val("dukeduke");
    $("#kc-login").click();

    $("#submit-review").should(Condition.appear);
    $("#submit-review").click();

    $("#review-submit").should(Condition.appear);
    $("#book-selection").click();
    $$(".visible .menu > div").get(0).click();
    $$("#book-rating > i").get(4).click();

    $("#review-title").val("Great Book about Software Development with Java!");
    $("#review-content").val("I really enjoyed reading this book. It contains great examples and discusses also advanced topics.");
    $("#review-submit").click();

    $("#all-reviews").click();
    $("#reviews").should(Condition.appear);
    $$("#reviews > div").shouldHaveSize(1);
    $("#review-0 .review-title").shouldHave(Condition.text("Great Book about Software Development with Java!"));
    $("#review-0 .review-content").shouldHave(Condition.text("I really enjoyed reading this book. It contains great examples and discusses also advanced topics."));
  }
}

