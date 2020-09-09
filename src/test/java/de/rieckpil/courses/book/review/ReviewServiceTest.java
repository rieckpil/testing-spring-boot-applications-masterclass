package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.book.management.User;
import de.rieckpil.courses.book.management.UserService;
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
  private ReviewVerifier reviewVerifier;

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
    assertNotNull(reviewRepository);
    assertNotNull(reviewVerifier);
    assertNotNull(userService);
    assertNotNull(bookRepository);
    assertNotNull(cut);
  }

  @Test
  public void shouldThrowExceptionWhenReviewedBookIsNotExisting() {
    when(bookRepository.findByIsbn(ISBN)).thenReturn(null);

    assertThrows(IllegalArgumentException.class,
      () -> cut.createBookReview(ISBN, null, USERNAME, EMAIL));
  }

  @Test
  public void shouldRejectReviewWhenReviewQualityIsBad() {

    BookReviewRequest bookReviewRequest =
      new BookReviewRequest("Title", "BADCONTENT!", 1);

    when(bookRepository.findByIsbn(ISBN)).thenReturn(new Book());
    when(reviewVerifier.doesMeetQualityStandards(bookReviewRequest.getReviewContent())).thenReturn(false);

    assertThrows(BadReviewQualityException.class,
      () -> cut.createBookReview(ISBN, bookReviewRequest, USERNAME, EMAIL));

    verify(reviewRepository, times(0)).save(ArgumentMatchers.any(Review.class));
  }

  @Test
  public void shouldStoreReviewWhenReviewQualityIsGoodAndBookIsPresent() {

    BookReviewRequest bookReviewRequest =
      new BookReviewRequest("Title", "GOOD CONTENT!", 1);

    when(bookRepository.findByIsbn(ISBN)).thenReturn(new Book());
    when(reviewVerifier.doesMeetQualityStandards(bookReviewRequest.getReviewContent())).thenReturn(true);
    when(userService.getOrCreateUser(USERNAME, EMAIL)).thenReturn(new User());
    when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
      Review reviewToSave = invocation.getArgument(0);
      reviewToSave.setId(42L);
      return reviewToSave;
    });

    Long result = cut.createBookReview(ISBN, bookReviewRequest, USERNAME, EMAIL);

    assertEquals(42, result);
  }
}
