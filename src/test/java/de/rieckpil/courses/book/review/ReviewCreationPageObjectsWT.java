package de.rieckpil.courses.book.review;

import java.io.File;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import de.rieckpil.courses.AbstractWebTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.pages.DashboardPage;
import de.rieckpil.courses.pages.LoginPage;
import de.rieckpil.courses.pages.NewReviewPage;
import de.rieckpil.courses.pages.ReviewListPage;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

class ReviewCreationPageObjectsWT extends AbstractWebTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  DashboardPage dashboardPage = new DashboardPage();
  LoginPage loginPage = new LoginPage();
  NewReviewPage newReviewPage = new NewReviewPage();
  ReviewListPage reviewListPage = new ReviewListPage();

  @Container
  static BrowserWebDriverContainer<?> webDriverContainer = new BrowserWebDriverContainer<>(
    // Workaround to allow running the tests on an Apple M1
    System.getProperty("os.arch").equals("aarch64") ?
      DockerImageName.parse("seleniarm/standalone-firefox")
        .asCompatibleSubstituteFor("selenium/standalone-firefox")
      : DockerImageName.parse("selenium/standalone-firefox")
  )
    .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("./target"))
    .withCapabilities(new FirefoxOptions());

  private static final String ISBN = "9780321751041";

  @BeforeEach
  void setup() {
    Configuration.timeout = 2000;
    Configuration.baseUrl = SystemUtils.IS_OS_LINUX ? "http://172.17.0.1:8080" : "http://host.docker.internal:8080";

    RemoteWebDriver remoteWebDriver = webDriverContainer.getWebDriver();
    WebDriverRunner.setWebDriver(remoteWebDriver);
  }

  @AfterEach
  void tearDown() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
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
