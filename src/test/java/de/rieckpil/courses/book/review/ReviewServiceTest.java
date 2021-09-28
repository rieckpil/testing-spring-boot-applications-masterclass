package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.book.management.User;
import de.rieckpil.courses.book.management.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewVerifier mockedReviewVerifier;

  @Mock
  private UserService userService;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private ReviewService cut;

  private static final String EMAIL = "duke@spring.io";
  private static final String USERNAME = "duke";
  private static final String ISBN = "42";

  @Test
  public void shouldNotBeNull() {
  }

  @Test
  @DisplayName("Write english sentence")
  public void shouldThrowExceptionWhenReviewedBookIsNotExisting() {
  }

  @Test
  public void shouldRejectReviewWhenReviewQualityIsBad() {
  }

  @Test
  public void shouldStoreReviewWhenReviewQualityIsGoodAndBookIsPresent() {
  }
}
