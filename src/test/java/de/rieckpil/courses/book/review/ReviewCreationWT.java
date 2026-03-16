package de.rieckpil.courses.book.review;

import java.io.File;
import java.time.Duration;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import de.rieckpil.courses.AbstractWebTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.selenium.BrowserWebDriverContainer;
import org.testcontainers.utility.DockerImageName;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReviewCreationWT extends AbstractWebTest {

  @Autowired private BookRepository bookRepository;

  @Autowired private ReviewRepository reviewRepository;

  @LocalServerPort private int port;

  @Container
  static BrowserWebDriverContainer webDriverContainer =
      new BrowserWebDriverContainer(
              System.getProperty("os.arch").equals("aarch64")
                  ? DockerImageName.parse("seleniarm/standalone-firefox")
                      .asCompatibleSubstituteFor("selenium/standalone-firefox")
                  : DockerImageName.parse("selenium/standalone-firefox"))
          .withRecordingMode(
              "true".equals(System.getenv("CI"))
                  ? BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING
                  : BrowserWebDriverContainer.VncRecordingMode.SKIP,
              new File("./target"))
          .withAccessToHost(true);

  private static final String ISBN = "9780321751041";

  @BeforeEach
  void setup() {
    Configuration.timeout = 10_000;

    Testcontainers.exposeHostPorts(port, 8888);
    Configuration.baseUrl = "http://host.testcontainers.internal:" + port;

    RemoteWebDriver remoteWebDriver =
        new RemoteWebDriver(webDriverContainer.getSeleniumAddress(), new FirefoxOptions(), false);
    remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    WebDriverRunner.setWebDriver(remoteWebDriver);

    createBook();
  }

  @AfterEach
  void tearDown() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
  }

  @Test
  void shouldCreateReviewAndDisplayItInReviewList() {
    assertNotNull(bookRepository);
    open("/");

    performLogin();
    submitReview();
    verifyReviewIsPartOfAllReviews();
  }

  private void verifyReviewIsPartOfAllReviews() {
    $("#all-reviews").click();
    $("#reviews").should(Condition.appear);
    $$("#reviews > div").shouldHave(CollectionCondition.size(1));
    $("#review-0 .review-title")
        .shouldHave(Condition.text("Great Book about Software Development with Java!"));
    $("#review-0 .review-content")
        .shouldHave(
            Condition.text(
                "I really enjoyed reading this book. It contains great examples and discusses also advanced topics."));
  }

  private void submitReview() {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click(usingJavaScript());

    screenshot("after_click_submit_review");
    $("#review-submit").should(Condition.appear);
    // Wait for the books dropdown to finish loading (first book is preselected automatically)
    $("#book-selection").should(Condition.enabled);

    $$("#book-rating label").get(5).click(usingJavaScript());

    $("#review-title").sendKeys("Great Book about Software Development with Java!");
    $("#review-content")
        .sendKeys(
            "I really enjoyed reading this book. It contains great examples and discusses also advanced topics.");

    screenshot("before_submit_review");

    $("#review-submit").click(usingJavaScript());
    $("[role='alert']").should(Condition.appear);
  }

  private void performLogin() {
    $("#login").click();
    $("#kc-login").should(Condition.appear);
    $("#username").val("duke");
    $("#password").val("dukeduke");

    screenshot("before_submit");

    $("#kc-login").click();
    $("#logout").should(Condition.appear);
  }

  private void createBook() {
    Book book = new Book();
    book.setPublisher("Duke Inc.");
    book.setIsbn(ISBN);
    book.setPages(42L);
    book.setTitle("Joyful testing with Spring Boot");
    book.setDescription("Writing unit and integration tests for Spring Boot applications");
    book.setAuthor("rieckpil");
    book.setThumbnailUrl(
        "https://rieckpil.de/wp-content/uploads/2020/08/tsbam_introduction_thumbnail-585x329.png.webp");
    book.setGenre("Software Development");

    this.bookRepository.save(book);
  }
}
