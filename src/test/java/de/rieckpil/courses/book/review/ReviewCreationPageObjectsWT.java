package de.rieckpil.courses.book.review;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import de.rieckpil.courses.AbstractWebTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.pages.DashboardPage;
import de.rieckpil.courses.pages.LoginPage;
import de.rieckpil.courses.pages.NewReviewPage;
import de.rieckpil.courses.pages.ReviewListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;

public class ReviewCreationPageObjectsWT extends AbstractWebTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  DashboardPage dashboardPage = new DashboardPage();
  LoginPage loginPage = new LoginPage();
  NewReviewPage newReviewPage = new NewReviewPage();
  ReviewListPage reviewListPage = new ReviewListPage();

  @Container
  static BrowserWebDriverContainer<?> webDriverContainer = new BrowserWebDriverContainer<>()
    .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("./target"))
    .withCapabilities(new FirefoxOptions());

  private static final String ISBN = "9780321751041";

  @BeforeEach
  public void setup() {
    Configuration.timeout = 2000;
    Configuration.baseUrl = "http://172.17.0.1:8080";

    RemoteWebDriver remoteWebDriver = webDriverContainer.getWebDriver();
    WebDriverRunner.setWebDriver(remoteWebDriver);
  }

  @AfterEach
  public void tearDown() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
  }

  @Test
  public void shouldCreateReviewAndDisplayItInReviewList() {
    createBook();

    String reviewTitle = "Great Book about Software Development with Java!";
    String reviewContent = "I really enjoyed reading this book. It contains great examples and discusses also advanced topics.";

    dashboardPage.open();
    loginPage.performLogin("duke", "dukeduke");
    newReviewPage.submitReview(reviewTitle, reviewContent, 0, 4);
    reviewListPage.shouldContainExactlyOneReview(reviewTitle, reviewContent);
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
