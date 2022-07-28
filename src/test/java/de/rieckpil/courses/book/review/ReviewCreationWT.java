package de.rieckpil.courses.book.review;

import java.io.File;
import java.util.logging.Level;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import java.util.logging.Level;

import de.rieckpil.courses.AbstractWebTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.screenshot;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReviewCreationWT extends AbstractWebTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private static final LoggingPreferences LOG_PREFERENCES;
  private static final ChromeOptions CHROME_OPTIONS;

  static {
    LOG_PREFERENCES = new LoggingPreferences();
    LOG_PREFERENCES.enable(LogType.BROWSER, Level.ALL);

    CHROME_OPTIONS = new ChromeOptions();
    CHROME_OPTIONS.addArguments("--no-sandbox");
    CHROME_OPTIONS.addArguments("--disable-dev-shm-usage");

    CHROME_OPTIONS.setCapability("goog:loggingPrefs", LOG_PREFERENCES);
  }

  @Container
  static BrowserWebDriverContainer<?> webDriverContainer = new BrowserWebDriverContainer<>(
    // Workaround to allow running the tests on an Apple M1
    System.getProperty("os.arch").equals("aarch64") ?
      DockerImageName.parse("seleniarm/standalone-chromium")
        .asCompatibleSubstituteFor("selenium/standalone-chrome")
      : DockerImageName.parse("selenium/standalone-chrome")
  )
    .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("./target"))
    .withCapabilities(CHROME_OPTIONS);

  private static final String ISBN = "9780321751041";

  @BeforeEach
  void setup() {
    Configuration.timeout = 2000;
    // TODO: Improve platform independence, see Testcontainers.exposeHostPorts https://rieckpil.de/write-concise-web-tests-with-selenide-for-java-projects/
    Configuration.baseUrl = SystemUtils.IS_OS_LINUX ? "http://172.17.0.1:8080" : "http://host.docker.internal:8080";

    RemoteWebDriver remoteWebDriver = webDriverContainer.getWebDriver();
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
  }

  private void createBook() {
    Book book = new Book();
    book.setPublisher("Duke Inc.");
    book.setIsbn(ISBN);
    book.setPages(42L);
    book.setTitle("Joyful testing with Spring Boot");
    book.setDescription("Writing unit and integration tests for Spring Boot applications");
    book.setAuthor("rieckpil");
    book.setThumbnailUrl("https://rieckpil.de/wp-content/uploads/2020/08/tsbam_introduction_thumbnail-585x329.png.webp");
    book.setGenre("Software Development");

    this.bookRepository.save(book);
  }
}
