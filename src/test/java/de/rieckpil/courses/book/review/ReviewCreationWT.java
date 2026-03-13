package de.rieckpil.courses.book.review;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.selenium.BrowserWebDriverContainer;
import org.testcontainers.utility.DockerImageName;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReviewCreationWT extends AbstractWebTest {

  @Autowired private BookRepository bookRepository;

  @Autowired private ReviewRepository reviewRepository;

  @LocalServerPort private int port;

  private static final LoggingPreferences LOG_PREFERENCES;
  private static final ChromeOptions CHROME_OPTIONS;

  static {
    LOG_PREFERENCES = new LoggingPreferences();
    LOG_PREFERENCES.enable(LogType.BROWSER, Level.ALL);
    LOG_PREFERENCES.enable(LogType.PERFORMANCE, Level.ALL);

    CHROME_OPTIONS = new ChromeOptions();
    CHROME_OPTIONS.addArguments("--no-sandbox");
    CHROME_OPTIONS.addArguments("--disable-dev-shm-usage");
    CHROME_OPTIONS.addArguments("--remote-allow-origins=*");
    // Prevent Chrome's "Save password?" and autofill bubbles from intercepting clicks
    CHROME_OPTIONS.addArguments("--disable-infobars");
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("credentials_enable_service", false);
    prefs.put("profile.password_manager_enabled", false);
    CHROME_OPTIONS.setExperimentalOption("prefs", prefs);

    CHROME_OPTIONS.setCapability("goog:loggingPrefs", LOG_PREFERENCES);
  }

  @Container
  static BrowserWebDriverContainer webDriverContainer =
      new BrowserWebDriverContainer(
              // Workaround to allow running the tests on an Apple M1
              System.getProperty("os.arch").equals("aarch64")
                  ? DockerImageName.parse("seleniarm/standalone-chromium:latest")
                      .asCompatibleSubstituteFor("selenium/standalone-chrome")
                  : DockerImageName.parse("selenium/standalone-chrome:latest"))
          .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, new File("./target"))
          .withAccessToHost(true);

  private static final String ISBN = "9780321751041";

  @BeforeEach
  void setup() {
    Configuration.timeout = 10_000;

    Testcontainers.exposeHostPorts(port, 8888);
    Configuration.baseUrl = "http://host.testcontainers.internal:" + port;

    RemoteWebDriver remoteWebDriver =
        new RemoteWebDriver(webDriverContainer.getSeleniumAddress(), CHROME_OPTIONS, false);
    remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    WebDriverRunner.setWebDriver(remoteWebDriver);

    createBook();
  }

  @AfterEach
  void tearDown() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();

    for (LogEntry logEntry : getWebDriver().manage().logs().get(LogType.BROWSER)) {
      LOG.info(logEntry.getMessage());
    }
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
    $("#submit-review").click();

    screenshot("after_click_submit_review");
    $("#review-submit").should(Condition.appear);
    $("#book-selection").click();
    $(".visible .menu").should(Condition.appear);
    $$(".visible .menu > div").get(0).click();
    $$("#book-rating > i").get(4).click();

    $("#review-title").val("Great Book about Software Development with Java!");
    $("#review-content")
        .val(
            "I really enjoyed reading this book. It contains great examples and discusses also advanced topics.");

    screenshot("before_submit_review");

    $("#review-submit").click();
    $(".ui .success").should(Condition.appear);
  }

  private void performLogin() {
    $("button.ui").click();
    $("#kc-login").should(Condition.appear);
    $("#username").val("duke");
    $("#password").val("dukeduke");

    screenshot("before_submit");

    $("#kc-login").click();
    // Wait for the post-login auth callback to fully settle before proceeding.
    // #submit-review appears briefly during the redirect, but clicks made then get
    // overridden when the React app completes the callback and navigates to #/.
    $("button.ui.red").should(Condition.appear);
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
