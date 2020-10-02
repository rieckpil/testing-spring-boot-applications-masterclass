package de.rieckpil.courses.book.review;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import de.rieckpil.courses.AbstractWebTest;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Selenide.*;

public class ReviewCreationWT extends AbstractWebTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private static final String ISBN = "9780321751041";

  @BeforeAll
  static void config() {
    Configuration.timeout = 2000;
  }

  @BeforeEach
  public void setup() {
    RemoteWebDriver remoteWebDriver = webDriverContainer.getWebDriver();
    WebDriverRunner.setWebDriver(remoteWebDriver);

    Configuration.baseUrl = "http://172.17.0.1:" + port;
  }

  @AfterEach
  public void tearDown() {
    WebDriverRunner.closeWebDriver();
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
  }

  @Test
  public void shouldCreateReviewAndDisplayItInReviewList() {

    open("/");

    createBook();

    performLogin();
    submitReview();
    verifyReviewIsPartOfAllReviews();
  }

  private void verifyReviewIsPartOfAllReviews() {
    $("#all-reviews").click();
    $("#reviews").should(Condition.appear);
    $$("#reviews > div").shouldHaveSize(1);
    $("#review-0 .review-title").shouldHave(Condition.text("Great Book about Software Development with Java!"));
    $("#review-0 .review-content").shouldHave(Condition.text("I really enjoyed reading this book. It contains great examples and discusses also advanced topics."));
  }

  private void submitReview() {
    $("#submit-review").should(Condition.appear);
    $("#submit-review").click();

    $("#review-submit").should(Condition.appear);
    $("#book-selection").click();
    $$(".visible .menu > div").get(0).click();
    $$("#book-rating > i").get(4).click();

    $("#review-title").val("Great Book about Software Development with Java!");
    $("#review-content").val("I really enjoyed reading this book. It contains great examples and discusses also advanced topics.");

    screenshot("before_submit");

    $("#review-submit").click();
    $(".ui .success").should(Condition.appear);
  }

  private void performLogin() {
    $("button.ui").click();
    $("#kc-login").should(Condition.appear);
    $("#username").val("duke");
    $("#password").val("dukeduke");
    $("#kc-login").click();
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
